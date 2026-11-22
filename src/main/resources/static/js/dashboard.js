let dashTab = 'bookings';
let fieldSeq = 0;
const pad2 = n => String(n).padStart(2, '0');
const localDateStr = d => d.getFullYear() + '-' + pad2(d.getMonth() + 1) + '-' + pad2(d.getDate());

function fld(label, input) {
  const id = 'df' + (++fieldSeq);
  input.id = id;
  const err = el('small', { class: 'err' });
  return { err, node: el('div', { class: 'field' }, el('label', { for: id, text: label }), input, err) };
}

function providerRegisterView(head, t) {
  const name = el('input', { type: 'text', maxlength: '100', autocomplete: 'organization' });
  const desc = el('textarea', { rows: '3', maxlength: '500' });
  const fn = fld('اسم النشاط أو مقدم الخدمة', name);
  const fd = fld('وصف مختصر (اختياري)', desc);
  const btn = el('button', { type: 'submit', class: 'btn block', text: 'ابدأ كمقدم خدمة' });
  const form = el('form', { novalidate: true }, fn.node, fd.node, btn);
  form.addEventListener('submit', async e => {
    e.preventDefault();
    fn.err.textContent = '';
    const n = name.value.trim();
    if (!n) { fn.err.textContent = 'هذا الحقل مطلوب'; return; }
    const body = { name: n };
    const d = desc.value.trim();
    if (d) body.description = d;
    btn.disabled = true; btn.classList.add('loading');
    const r = await Api.post('/providers', body);
    if (r.ok || (r.error && r.error.code === 'PROVIDER_ALREADY_EXISTS')) {
      toast('تم تفعيل حسابك كمقدم خدمة', 'ok');
      if (!isStale(t)) dashboardView();
      return;
    }
    btn.disabled = false; btn.classList.remove('loading');
    (r.error.fieldErrors || []).forEach(x => { if (x.field === 'name') fn.err.textContent = 'قيمة غير صالحة'; });
    toast(r.error.message, 'error');
  });
  mount(head, el('section', { class: 'auth card' },
    el('h1', { text: 'كن مقدم خدمة' }),
    el('p', { class: 'sub', text: 'سجّل نشاطك لتضيف خدماتك وأوقاتك المتاحة ويبدأ العملاء بالحجز' }),
    form));
}

function serviceItem(s) {
  return el('div', { class: 'card d-item' },
    el('div', { class: 'b-top' },
      el('b', { text: s.name }),
      el('span', { class: 'badge ' + (s.active ? 'confirmed' : 'cancelled'), text: s.active ? 'نشطة' : 'موقوفة' })),
    s.description ? el('p', { class: 'muted', text: s.description }) : null,
    el('div', { class: 'meta' },
      el('span', { class: 'pill' }, ico('clock'), fmtDuration(s.durationMinutes)),
      el('span', { class: 'pill', text: fmtPrice(s.price) })));
}

function availItem(a) {
  const s = toDate(a.startTime), e = toDate(a.endTime);
  const open = a.status === 'OPEN';
  return el('div', { class: 'card d-item' + (e.getTime() <= Date.now() ? ' past' : '') },
    el('div', { class: 'b-top' },
      el('b', { text: fmtWeekday(s) + ' ' + fmtDayMonth(s) }),
      el('span', { class: 'badge ' + (open ? 'confirmed' : 'cancelled'), text: open ? 'مفتوح' : 'مغلق' })),
    el('p', { class: 'b-time' }, ico('clock'), fmtTime(s) + ' ← ' + fmtTime(e)));
}

function addServiceSheet(onAdded) {
  const name = el('input', { type: 'text', maxlength: '100' });
  const desc = el('textarea', { rows: '2', maxlength: '500' });
  const price = el('input', { type: 'number', min: '0', step: '0.01', inputmode: 'decimal', dir: 'ltr' });
  const dur = el('input', { type: 'number', min: '1', step: '1', inputmode: 'numeric', dir: 'ltr', value: '30' });
  const fn = fld('اسم الخدمة', name), fdsc = fld('الوصف (اختياري)', desc);
  const fp = fld('السعر (' + CURRENCY + ')', price), fdu = fld('المدة بالدقائق', dur);
  const btn = el('button', { type: 'submit', class: 'btn block', text: 'حفظ الخدمة' });
  const form = el('form', { novalidate: true }, fn.node, fdsc.node, el('div', { class: 'row2' }, fp.node, fdu.node), btn);
  let sheet;
  form.addEventListener('submit', async e => {
    e.preventDefault();
    [fn, fp, fdu].forEach(f => { f.err.textContent = ''; });
    let ok = true;
    const n = name.value.trim();
    const p = price.value.trim() === '' ? NaN : Number(price.value);
    const m = dur.value.trim() === '' ? NaN : Number(dur.value);
    if (!n) { fn.err.textContent = 'هذا الحقل مطلوب'; ok = false; }
    if (!(p >= 0)) { fp.err.textContent = 'أدخل سعرًا صحيحًا'; ok = false; }
    if (!Number.isInteger(m) || m <= 0) { fdu.err.textContent = 'أدخل مدة صحيحة بالدقائق'; ok = false; }
    if (!ok) return;
    const body = { name: n, price: p, durationMinutes: m };
    const d = desc.value.trim();
    if (d) body.description = d;
    btn.disabled = true; btn.classList.add('loading');
    const r = await Api.post('/services', body);
    btn.disabled = false; btn.classList.remove('loading');
    if (r.ok) { toast('تمت إضافة الخدمة', 'ok'); sheet.close(); onAdded(r.data); return; }
    toast(r.error.message, 'error');
  });
  sheet = openSheet('إضافة خدمة', form);
}

function addAvailabilitySheet(onAdded) {
  const tomorrow = new Date(Date.now() + 86400000);
  const date = el('input', { type: 'date', min: localDateStr(new Date()), value: localDateStr(tomorrow), dir: 'ltr' });
  const from = el('input', { type: 'time', value: '09:00', dir: 'ltr' });
  const to = el('input', { type: 'time', value: '17:00', dir: 'ltr' });
  const fdt = fld('اليوم', date), ff = fld('من', from), ft = fld('إلى', to);
  const btn = el('button', { type: 'submit', class: 'btn block', text: 'حفظ الوقت المتاح' });
  const form = el('form', { novalidate: true }, fdt.node, el('div', { class: 'row2' }, ff.node, ft.node),
    el('small', { class: 'muted', text: 'الأوقات بتوقيت جهازك' }), btn);
  let sheet;
  form.addEventListener('submit', async e => {
    e.preventDefault();
    [fdt, ff, ft].forEach(f => { f.err.textContent = ''; });
    if (!date.value || !from.value || !to.value) {
      if (!date.value) fdt.err.textContent = 'هذا الحقل مطلوب';
      if (!from.value) ff.err.textContent = 'هذا الحقل مطلوب';
      if (!to.value) ft.err.textContent = 'هذا الحقل مطلوب';
      return;
    }
    const s = new Date(date.value + 'T' + from.value);
    const en = new Date(date.value + 'T' + to.value);
    if (isNaN(s) || isNaN(en)) { toast('التاريخ أو الوقت غير صالح', 'error'); return; }
    if (en <= s) { ft.err.textContent = 'وقت النهاية لازم يكون بعد البداية'; return; }
    if (s.getTime() <= Date.now()) { ff.err.textContent = 'اختر وقتًا في المستقبل'; return; }
    btn.disabled = true; btn.classList.add('loading');
    const r = await Api.post('/availabilities', { startTime: s.toISOString(), endTime: en.toISOString() });
    btn.disabled = false; btn.classList.remove('loading');
    if (r.ok) { toast('تمت إضافة الوقت المتاح', 'ok'); sheet.close(); onAdded(r.data); return; }
    toast(r.error.message, 'error');
  });
  sheet = openSheet('إضافة وقت متاح', form);
}

function kpiBox(val, label, accent) {
  return el('div', { class: 'kpi' + (accent ? ' accent' : '') },
    el('span', { class: 'kpi-val', text: fmtNum(val) }),
    el('span', { class: 'kpi-lbl', text: label }));
}

async function dashboardView() {
  const t = beginView();
  const head = pageHead('لوحة مقدم الخدمة', 'أدر طلبات الحجز، خدماتك، وأوقاتك المتاحة');
  mount(head, skeletonCards(2));
  const me = await Api.get('/providers/me');
  if (isStale(t)) return;
  if (!me.ok) {
    if (me.status === 404 || me.status === 403) { providerRegisterView(pageHead('لوحة مقدم الخدمة'), t); return; }
    mount(head, errorState(me.error.message, dashboardView));
    return;
  }
  const provider = me.data;
  const [sv, av, bk] = await Promise.all([Api.get('/services/mine'), Api.get('/availabilities/mine'), Api.get('/bookings/provider')]);
  if (isStale(t)) return;
  const failed = [sv, av, bk].find(x => !x.ok);
  if (failed) { mount(head, errorState(failed.error.message, dashboardView)); return; }

  let services = asList(sv.data);
  let avails = asList(av.data);
  let bookings = asList(bk.data);
  const kpis = el('div', { class: 'kpi-grid' });
  const tabs = el('div', { class: 'tabs', role: 'tablist' });
  const panel = el('div', { class: 'd-panel' });

  const info = el('div', { class: 'card p-head' },
    avatar(provider.name),
    el('div', {},
      el('h2', { class: 'p-name', text: provider.name }),
      el('p', { class: 'muted', text: provider.description || 'لا يوجد وصف' })));

  const note = provider.status && provider.status !== 'ACTIVE'
    ? el('p', { class: 'note' }, ico('alert'), 'حسابك غير مفعّل لاستقبال الحجوزات حاليًا، فلن يظهر للعملاء.')
    : null;

  const tabBtn = (key, label, count) => el('button', {
    type: 'button',
    role: 'tab',
    class: 'tab' + (dashTab === key ? ' on' : ''),
    'aria-selected': String(dashTab === key),
    onclick: () => { dashTab = key; paint(); }
  }, label + ' (' + fmtNum(count) + ')');

  function paint() {
    const now = Date.now();
    const pendingCnt = bookings.filter(b => b.status === 'PENDING' && toDate(b.endTime).getTime() > now).length;
    const confirmedCnt = bookings.filter(b => b.status === 'CONFIRMED' && toDate(b.endTime).getTime() > now).length;
    const activeSvcCnt = services.filter(s => s.active !== false).length;
    const openAvailCnt = avails.filter(a => a.status === 'OPEN' && toDate(a.endTime).getTime() > now).length;

    kpis.replaceChildren(
      kpiBox(pendingCnt, 'طلبات بانتظار ردك', pendingCnt > 0),
      kpiBox(confirmedCnt, 'حجوزات مؤكدة قادمة', false),
      kpiBox(activeSvcCnt, 'خدمات نشطة', false),
      kpiBox(openAvailCnt, 'أوقات متاحة قادمة', false));

    tabs.replaceChildren(
      tabBtn('bookings', 'الحجوزات', bookings.length),
      tabBtn('services', 'الخدمات', services.length),
      tabBtn('availability', 'الأوقات المتاحة', avails.length));

    if (dashTab === 'bookings') {
      panel.replaceChildren(providerBookingsPanel(bookings, paint));
    } else if (dashTab === 'services') {
      panel.replaceChildren(
        el('div', { class: 'toolbar' },
          el('b', { text: 'خدماتي' }),
          el('button', {
            type: 'button',
            class: 'btn',
            onclick: () => addServiceSheet(s => { services = [s, ...services]; paint(); })
          }, ico('plus'), 'إضافة خدمة')),
        services.length
          ? el('div', { class: 'd-list' }, services.map(serviceItem))
          : emptyState('wrench', 'لم تضف خدمات بعد', 'أضف خدمتك الأولى ليتمكن العملاء من حجزها'));
    } else {
      const sorted = [...avails].sort((a, c) => toDate(a.startTime) - toDate(c.startTime));
      panel.replaceChildren(
        el('div', { class: 'toolbar' },
          el('b', { text: 'أوقاتي المتاحة' }),
          el('button', {
            type: 'button',
            class: 'btn',
            onclick: () => addAvailabilitySheet(a => { avails = [...avails, a]; paint(); })
          }, ico('plus'), 'إضافة وقت')),
        sorted.length
          ? el('div', { class: 'd-list' }, sorted.map(availItem))
          : emptyState('calendar', 'لم تضف أوقاتًا متاحة بعد', 'حدد الأوقات التي تستقبل فيها الحجوزات'));
    }
  }

  paint();
  mount(head, info, note, kpis, tabs, panel);
}