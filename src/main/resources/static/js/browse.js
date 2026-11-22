const asList = d => (Array.isArray(d) ? d : []);

async function loadSlots(serviceId) {
  const r = await Api.get('/services/' + serviceId + '/slots');
  if (!r.ok) return { ok: false, error: r.error };
  const list = asList(r.data).map(s => toDate(s.startTime).getTime()).filter(x => !isNaN(x)).sort((a, c) => a - c);
  return { ok: true, list };
}

function pageHead(title, sub) {
  return el('div', { class: 'page-head' },
    el('h1', { text: title }),
    sub ? el('p', { class: 'muted', text: sub }) : null);
}

function backLink() {
  return el('a', { class: 'back', href: '#/providers' }, ico('arrowRight'), 'كل مقدمي الخدمة');
}

function skeletonCards(n) {
  return el('div', { class: 'grid provider-grid' },
    Array.from({ length: n }, () => el('div', { class: 'card skeleton', 'aria-hidden': 'true' })));
}

function emptyState(iconName, title, sub) {
  return el('div', { class: 'card empty' },
    el('div', { class: 'empty-ico' }, ICONS[iconName] ? ico(iconName) : iconName),
    el('b', { text: title }),
    sub ? el('p', { class: 'muted', text: sub }) : null);
}

function errorState(message, retry) {
  return el('div', { class: 'card empty' },
    el('div', { class: 'empty-ico' }, ico('alert')),
    el('b', { text: message }),
    el('button', { type: 'button', class: 'btn ghost', onclick: () => retry(), text: 'إعادة المحاولة' }));
}

function avatar(name) {
  return el('div', { class: 'avatar ' + avatarTone(name), text: (name || '?').trim().charAt(0).toUpperCase() });
}

function providerCard(p) {
  return el('a', { class: 'card p-card', href: '#/provider/' + p.id },
    el('div', { class: 'p-card-top' },
      avatar(p.name),
      el('div', { class: 'p-body' },
        el('b', { text: p.name }),
        el('p', { class: 'muted clamp', text: p.description || 'لا يوجد وصف' }))),
    el('span', { class: 'go' }, 'عرض الخدمات والمواعيد', ico('arrowLeft')));
}

async function providersView() {
  const t = beginView();
  const head = pageHead('مقدمو الخدمة', 'اختر مقدم خدمة لتشاهد خدماته ومواعيده المتاحة');
  mount(head, skeletonCards(6));
  const r = await Api.get('/providers');
  if (isStale(t)) return;
  if (!r.ok) { mount(head, errorState(r.error.message, providersView)); return; }
  const all = asList(r.data);
  if (!all.length) { mount(head, emptyState('search', 'لا يوجد مقدمو خدمة حاليًا', 'جرّب مرة أخرى لاحقًا')); return; }

  const grid = el('div', { class: 'grid provider-grid' });
  const count = el('span', { class: 'search-count' });
  const search = el('input', { class: 'search', type: 'search', placeholder: 'ابحث بالاسم أو التخصص أو الوصف...', 'aria-label': 'بحث' });
  const searchWrap = el('div', { class: 'search-wrap' },
    el('span', { class: 'search-ico' }, ico('search')),
    search,
    count);

  const paint = () => {
    const q = search.value.trim().toLowerCase();
    const list = all.filter(p => !q || (p.name || '').toLowerCase().includes(q) || (p.description || '').toLowerCase().includes(q));
    count.textContent = fmtNum(list.length) + ' / ' + fmtNum(all.length);
    grid.replaceChildren(...(list.length ? list.map(providerCard) : [emptyState('search', 'لا توجد نتائج مطابقة لبحثك')]));
  };
  search.addEventListener('input', paint);
  paint();
  mount(head, searchWrap, grid);
}

function serviceCard(s, slots, onBook) {
  const has = slots.length > 0;
  const next = has ? 'أقرب موعد: ' + fmtDateTime(new Date(slots[0])) : 'لا توجد مواعيد متاحة حاليًا';
  return el('div', { class: 'card svc' },
    el('div', { class: 'svc-top' },
      el('b', { text: s.name }),
      el('span', { class: 'price', text: fmtPrice(s.price) })),
    s.description ? el('p', { class: 'muted clamp', text: s.description }) : null,
    el('div', { class: 'meta' },
      el('span', { class: 'pill' }, ico('clock'), fmtDuration(s.durationMinutes))),
    el('div', { class: 'svc-foot' },
      el('small', { class: has ? 'next' : 'muted', text: next }),
      el('button', { type: 'button', class: 'btn block', disabled: !has, onclick: onBook, text: has ? 'احجز الآن' : 'غير متاح حاليًا' })));
}

async function providerView(id) {
  const t = beginView();
  mount(backLink(), skeletonCards(3));
  const [pr, sv] = await Promise.all([
    Api.get('/providers'),
    Api.get('/providers/' + id + '/services')
  ]);
  if (isStale(t)) return;
  const failed = [pr, sv].find(x => !x.ok);
  if (failed) { mount(backLink(), errorState(failed.error.message, () => providerView(id))); return; }
  const provider = asList(pr.data).find(p => p.id === id);
  if (!provider) { mount(backLink(), emptyState('search', 'مقدم الخدمة غير موجود', 'ربما لا يقبل حجوزات حاليًا')); return; }
  const services = asList(sv.data).filter(s => s.active !== false);
  const loaded = await Promise.all(services.map(s => loadSlots(s.id)));
  if (isStale(t)) return;
  const bad = loaded.find(x => !x.ok);
  if (bad) { mount(backLink(), errorState(bad.error.message, () => providerView(id))); return; }
  const box = el('div', { class: 'grid svc-grid' }, services.map((s, i) =>
    serviceCard(s, loaded[i].list, () => openBooking(provider, s, loaded[i].list, () => providerView(id)))));
  mount(
    backLink(),
    el('div', { class: 'card p-head' },
      avatar(provider.name),
      el('div', {},
        el('h1', { text: provider.name }),
        el('p', { class: 'muted', text: provider.description || 'لا يوجد وصف' }))),
    el('h2', { class: 'section-title' }, 'الخدمات المتاحة', el('span', { class: 'pill', text: fmtNum(services.length) })),
    services.length ? box : emptyState('wrench', 'لا توجد خدمات متاحة حاليًا'));
}

function openBooking(provider, service, initialSlots, onBooked) {
  let slots = initialSlots;
  let day = 0, picked = null;
  const body = el('div', { class: 'book' });
  const sheet = openSheet('حجز ' + service.name, body);

  function draw() {
    const days = groupByDay(slots.filter(ms => ms > Date.now()));
    if (!days.length) {
      body.replaceChildren(el('p', { class: 'muted', text: 'لا توجد مواعيد متاحة حاليًا لهذه الخدمة.' }));
      return;
    }
    if (day >= days.length) day = 0;
    if (picked !== null && !days[day].slots.includes(picked)) picked = null;
    const info = el('div', { class: 'book-info' },
      el('span', { class: 'pill' }, ico('clock'), fmtDuration(service.durationMinutes)),
      el('span', { class: 'price', text: fmtPrice(service.price) }));
    const chips = el('div', { class: 'chips' }, days.map((d, i) =>
      el('button', {
        type: 'button',
        class: 'chip' + (i === day ? ' on' : ''),
        'aria-pressed': String(i === day),
        onclick: () => { day = i; picked = null; draw(); }
      },
      el('b', { text: fmtWeekday(d.date) }),
      el('small', { text: fmtDayMonth(d.date) }))));
    const grid = el('div', { class: 'slots' }, days[day].slots.map(ms =>
      el('button', {
        type: 'button',
        class: 'slot' + (ms === picked ? ' on' : ''),
        'aria-pressed': String(ms === picked),
        onclick: () => { picked = ms; draw(); },
        text: fmtTime(new Date(ms))
      })));
    const summary = el('p', { class: 'summary', text: picked === null ? 'اختر موعدًا للمتابعة' : fmtDateTime(new Date(picked)) });
    const go = el('button', {
      type: 'button',
      class: 'btn block',
      disabled: picked === null,
      onclick: e => submitBooking(e.currentTarget),
      text: 'تأكيد الحجز'
    });
    body.replaceChildren(info, chips, grid, el('small', { class: 'muted', text: 'الأوقات بتوقيت جهازك' }), summary, go);
  }

  async function submitBooking(btn) {
    btn.disabled = true;
    btn.classList.add('loading');
    const r = await Api.post('/bookings', { serviceId: service.id, startTime: new Date(picked).toISOString() });
    if (r.ok) {
      toast('تم إرسال طلب الحجز، في انتظار موافقة مقدم الخدمة', 'ok');
      sheet.close();
      onBooked();
      return;
    }
    if (['BOOKING_SLOT_TAKEN', 'SLOT_NOT_AVAILABLE'].includes(r.error.code)) {
      const fresh = await loadSlots(service.id);
      if (fresh.ok) slots = fresh.list;
      picked = null;
    }
    toast(r.error.message, 'error');
    draw();
  }

  draw();
}