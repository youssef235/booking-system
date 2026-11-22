const PB_NO_OP = 'لا يمكن تنفيذ هذه العملية في حالة الحجز الحالية';
const customerLabel = b => b.customerName || ('عميل رقم ' + fmtNum(b.userId));

async function bookingAction(b, action, okMsg, buttons) {
  buttons.forEach(x => { x.disabled = true; });
  const r = await Api.post('/bookings/' + b.id + '/' + action);
  if (r.ok) { b.status = r.data.status; toast(okMsg, 'ok'); return true; }
  buttons.forEach(x => { x.disabled = false; });
  toast(r.error.code === 'INVALID_REQUEST' ? PB_NO_OP : r.error.message, 'error');
  return false;
}

function askSheet(title, text, yesLabel, run, repaint) {
  let sheet;
  const no = el('button', { type: 'button', class: 'btn ghost', text: 'تراجع', onclick: () => sheet.close() });
  const yes = el('button', { type: 'button', class: 'btn danger-solid', text: yesLabel, onclick: async () => {
    yes.classList.add('loading');
    no.disabled = true;
    const ok = await run([yes]);
    yes.classList.remove('loading');
    no.disabled = false;
    if (ok) { sheet.close(); repaint(); }
  } });
  sheet = openSheet(title, el('div', { class: 'book' }, el('p', { text }), el('div', { class: 'row-actions' }, no, yes)));
}

function pBookingCard(b, repaint) {
  const now = Date.now();
  const ended = toDate(b.endTime).getTime() <= now;
  const started = toDate(b.startTime).getTime() <= now;
  const st = String(b.status || '').toLowerCase();

  const direct = (label, action, msg, cls) => el('button', {
    type: 'button',
    class: 'btn sm ' + cls,
    text: label,
    onclick: async e => {
      const btn = e.currentTarget;
      if (await bookingAction(b, action, msg, [btn])) repaint();
    }
  });

  const asking = (label, action, msg, title) => el('button', {
    type: 'button',
    class: 'btn sm ghost danger',
    text: label,
    onclick: () => askSheet(
      title,
      customerLabel(b) + ' — ' + (b.serviceName || 'الخدمة') + ' — ' + fmtDateTime(toDate(b.startTime)),
      label,
      btns => bookingAction(b, action, msg, btns),
      repaint
    )
  });

  const acts = [];
  if (b.status === 'PENDING' && !ended) {
    acts.push(direct('قبول', 'confirm', 'تم قبول الحجز', ''));
    acts.push(asking('رفض', 'reject', 'تم رفض الحجز', 'رفض الحجز'));
  } else if (b.status === 'CONFIRMED' && !started) {
    acts.push(asking('إلغاء', 'cancel', 'تم إلغاء الحجز', 'إلغاء الحجز'));
  } else if (b.status === 'CONFIRMED' && ended) {
    acts.push(direct('تم تنفيذ الخدمة', 'complete', 'تم إكمال الحجز', 'ink'));
  }

  return el('div', { class: 'card b-card status-' + st },
    el('div', { class: 'b-top' },
      el('b', { text: b.serviceName || 'خدمة' }),
      el('span', { class: 'badge ' + st, text: STATUS_AR[b.status] || b.status })),
    el('p', { class: 'b-meta' }, ico('user'), customerLabel(b)),
    el('p', { class: 'b-time' }, ico('calendar'), fmtDateTime(toDate(b.startTime)) + ' ← ' + fmtTime(toDate(b.endTime))),
    acts.length ? el('div', { class: 'b-actions' }, acts) : null);
}

function providerBookingsPanel(bookings, repaint) {
  const now = Date.now();
  const ended = b => toDate(b.endTime).getTime() <= now;
  const live = b => (b.status === 'PENDING' || b.status === 'CONFIRMED') && !ended(b);
  const start = b => toDate(b.startTime).getTime();
  const groups = [
    ['بانتظار ردك', bookings.filter(b => b.status === 'PENDING' && !ended(b)).sort((a, c) => start(a) - start(c))],
    ['مؤكدة قادمة', bookings.filter(b => b.status === 'CONFIRMED' && !ended(b)).sort((a, c) => start(a) - start(c))],
    ['السابقة', bookings.filter(b => !live(b)).sort((a, c) => start(c) - start(a))]
  ].filter(g => g[1].length);

  if (!groups.length) return emptyState('inbox', 'لا توجد حجوزات بعد', 'ستظهر هنا طلبات الحجز عندما يحجز العملاء');

  return el('div', { class: 'pb' }, groups.map(([title, list]) =>
    el('section', { class: 'pb-group' },
      el('h3', { class: 'section-title' }, title, el('span', { class: 'pill', text: fmtNum(list.length) })),
      el('div', { class: 'b-list' }, list.map(x => pBookingCard(x, repaint))))));
}