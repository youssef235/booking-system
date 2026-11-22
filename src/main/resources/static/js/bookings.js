const STATUS_AR = { PENDING: 'بانتظار الموافقة', CONFIRMED: 'مؤكد', CANCELLED: 'ملغي', REJECTED: 'مرفوض', COMPLETED: 'مكتمل' };
let bookingsTab = 'upcoming';

const isActiveStatus = b => b.status === 'PENDING' || b.status === 'CONFIRMED';
const isUpcoming = b => isActiveStatus(b) && toDate(b.endTime).getTime() > Date.now();
const canCancelBooking = b => isActiveStatus(b) && toDate(b.startTime).getTime() > Date.now();
const startMs = b => toDate(b.startTime).getTime();

function bookingCard(b, onCancel) {
  const st = String(b.status || '').toLowerCase();
  return el('div', { class: 'card b-card status-' + st },
    el('div', { class: 'b-top' },
      el('b', { text: b.serviceName || 'خدمة' }),
      el('span', { class: 'badge ' + st, text: STATUS_AR[b.status] || b.status })),
    el('p', { class: 'b-meta' }, ico('briefcase'), b.providerName || 'مقدم خدمة'),
    el('p', { class: 'b-time' }, ico('calendar'), fmtDateTime(toDate(b.startTime)) + ' ← ' + fmtTime(toDate(b.endTime))),
    canCancelBooking(b)
      ? el('button', { type: 'button', class: 'btn sm ghost danger', onclick: () => onCancel(b), text: 'إلغاء الحجز' })
      : null);
}

async function bookingsView() {
  const t = beginView();
  const head = pageHead('حجوزاتي', 'تابع حالة حجوزاتك القادمة والسابقة وألغِ ما لا تحتاجه');
  mount(head, skeletonCards(3));
  const r = await Api.get('/bookings/mine');
  if (isStale(t)) return;
  if (!r.ok) { mount(head, errorState(r.error.message, bookingsView)); return; }
  const all = asList(r.data);
  const tabs = el('div', { class: 'tabs', role: 'tablist' });
  const list = el('div', { class: 'b-list' });

  const tabBtn = (key, label, count) => el('button', {
    type: 'button',
    role: 'tab',
    class: 'tab' + (bookingsTab === key ? ' on' : ''),
    'aria-selected': String(bookingsTab === key),
    onclick: () => { bookingsTab = key; paint(); }
  }, label + ' (' + fmtNum(count) + ')');

  function paint() {
    const up = all.filter(isUpcoming).sort((a, c) => startMs(a) - startMs(c));
    const past = all.filter(x => !isUpcoming(x)).sort((a, c) => startMs(c) - startMs(a));
    tabs.replaceChildren(tabBtn('upcoming', 'القادمة', up.length), tabBtn('past', 'السابقة', past.length));
    const cur = bookingsTab === 'upcoming' ? up : past;
    list.replaceChildren(...(cur.length
      ? cur.map(x => bookingCard(x, confirmCancel))
      : [emptyState('inbox', bookingsTab === 'upcoming' ? 'لا توجد حجوزات قادمة' : 'لا توجد حجوزات سابقة',
          bookingsTab === 'upcoming' ? 'تصفّح مقدمي الخدمة واحجز موعدك الأول' : null)]));
  }

  function confirmCancel(b) {
    const no = el('button', { type: 'button', class: 'btn ghost', text: 'تراجع', onclick: () => sheet.close() });
    const yes = el('button', { type: 'button', class: 'btn danger-solid', text: 'نعم، ألغِ الحجز', onclick: async () => {
      yes.disabled = true; no.disabled = true; yes.classList.add('loading');
      const res = await Api.post('/bookings/' + b.id + '/cancel');
      if (res.ok) {
        b.status = res.data.status;
        toast('تم إلغاء الحجز', 'ok');
        sheet.close();
        paint();
        return;
      }
      yes.disabled = false; no.disabled = false; yes.classList.remove('loading');
      toast(res.error.code === 'INVALID_REQUEST' ? 'لا يمكن إلغاء هذا الحجز في حالته الحالية' : res.error.message, 'error');
    } });
    const sheet = openSheet('إلغاء الحجز',
      el('div', { class: 'book' },
        el('p', { text: (b.serviceName || 'الخدمة') + ' — ' + fmtDateTime(toDate(b.startTime)) }),
        el('div', { class: 'row-actions' }, no, yes)));
  }

  paint();
  mount(head, tabs, list);
}