function isAuthed() {
  const s = Api.session.get();
  return !!s && new Date(s.expiresAt) > new Date();
}

function field(name, label, type, ac, ltr) {
  return `<div class="field"><label for="${name}">${label}</label>
    <input id="${name}" name="${name}" type="${type}" autocomplete="${ac}"${ltr ? ' dir="ltr"' : ''}>
    <small class="err" data-for="${name}"></small></div>`;
}

function validate(form) {
  let ok = true;
  form.querySelectorAll('input').forEach(i => {
    const e = form.querySelector(`[data-for="${i.name}"]`);
    e.textContent = '';
    if (!i.value.trim()) { e.textContent = 'هذا الحقل مطلوب'; ok = false; }
    else if (i.type === 'email' && !/^\S+@\S+\.\S+$/.test(i.value.trim())) { e.textContent = 'البريد الإلكتروني غير صالح'; ok = false; }
  });
  return ok;
}

async function submit(form, call, onOk) {
  if (!validate(form)) return;
  const btn = form.querySelector('button[type=submit]');
  const data = Object.fromEntries(new FormData(form));
  btn.disabled = true; btn.classList.add('loading');
  const r = await call(data);
  btn.disabled = false; btn.classList.remove('loading');
  if (r.ok) { onOk(r.data); return; }
  const fe = r.error.fieldErrors || [];
  fe.forEach(x => { const box = form.querySelector(`[data-for="${x.field}"]`); if (box) box.textContent = 'قيمة غير صالحة'; });
  toast(r.error.message, 'error');
}

function authView(mode) {
  beginView();
  const login = mode === 'login';
  $app.innerHTML = `<section class="auth-shell">
    <aside class="auth-poster">
      <div>
        <span class="poster-kicker">مواعيد · دفتر الحجوزات</span>
        <h2>وقتك منظّم بوضوح، بدون رسائل متكررة</h2>
        <p>احجز خدمتك في ثوانٍ، أو أدر جدولك واستقبل طلبات عملائك من لوحة واحدة مرتبة.</p>
      </div>
      <ul class="poster-points">
        <li><span class="num">01</span><span>مواعيد متاحة محدّثة لحظيًا بتوقيت جهازك</span></li>
        <li><span class="num">02</span><span>متابعة فورية لحالة كل حجز من الطلب حتى التنفيذ</span></li>
        <li><span class="num">03</span><span>لوحة مخصصة لمقدمي الخدمة لإدارة الخدمات والأوقات</span></li>
      </ul>
    </aside>
    <div class="auth-panel">
      <h1>${login ? 'تسجيل الدخول' : 'إنشاء حساب جديد'}</h1>
      <p class="sub">${login ? 'أهلاً بعودتك، أدخل بياناتك للمتابعة' : 'ابدأ بحجز مواعيدك أو قدّم خدماتك في دقيقة'}</p>
      <form id="f" novalidate>
        ${login ? '' : field('name', 'الاسم', 'text', 'name')}
        ${field('email', 'البريد الإلكتروني', 'email', 'email', true)}
        ${field('password', 'كلمة المرور', 'password', login ? 'current-password' : 'new-password', true)}
        <button class="btn block" type="submit">${login ? 'دخول' : 'إنشاء الحساب'}</button>
      </form>
      <p class="alt">${login ? 'ما عندكش حساب؟ <a href="#/register">سجّل الآن</a>' : 'عندك حساب؟ <a href="#/login">سجّل دخولك</a>'}</p>
    </div>
  </section>`;

  const call = login
    ? d => Api.post('/auth/login', d)
    : async d => {
        const r = await Api.post('/auth/register', d);
        return r.ok ? Api.post('/auth/login', { email: d.email, password: d.password }) : r;
      };
  document.getElementById('f').addEventListener('submit', e => {
    e.preventDefault();
    submit(e.target, call, s => { Api.session.set(s); toast('أهلاً بك', 'ok'); location.hash = '#/home'; });
  });
}

function bentoTile(opts) {
  return el('a', { class: 'bento-card' + (opts.featured ? ' featured' : ''), href: opts.href },
    el('div', { class: 'bento-top' },
      el('span', { class: 'bento-ico' }, ico(opts.icon)),
      el('span', { class: 'bento-idx', text: opts.idx })),
    el('div', { class: 'bento-body' },
      el('b', { text: opts.title }),
      el('small', { text: opts.sub })),
    el('span', { class: 'bento-go' }, opts.cta, ico('arrowLeft')));
}

function homeView() {
  beginView();
  const s = Api.session.get();
  const who = (s.user && (s.user.name || s.user.email)) || '';
  const today = new Date();

  const masthead = el('section', { class: 'masthead' },
    el('div', {},
      el('span', { class: 'eyebrow', text: 'لوحة البداية' }),
      el('h1', { text: 'أهلاً ' + who }),
      el('p', { class: 'masthead-sub', text: 'كل مواعيدك، خدماتك، وحجوزاتك في مكان واحد مرتّب.' })),
    el('div', { class: 'date-stamp' }, ico('calendar'), fmtWeekday(today) + ' · ' + fmtDayMonth(today)));

  const bento = el('section', { class: 'bento' },
    bentoTile({
      featured: true,
      href: '#/providers',
      icon: 'search',
      idx: '01',
      title: 'تصفّح مقدمي الخدمة واحجز موعدك',
      sub: 'استعرض الأنشطة المتاحة، قارن الخدمات والأسعار، واختر أقرب وقت يناسبك مباشرة.',
      cta: 'استكشف مقدمي الخدمة'
    }),
    bentoTile({
      href: '#/bookings',
      icon: 'calendar',
      idx: '02',
      title: 'حجوزاتي',
      sub: 'تابع حالة حجوزاتك القادمة والسابقة، أو ألغِ ما لم تعد تحتاجه بسهولة.',
      cta: 'عرض جدول حجوزاتي'
    }),
    bentoTile({
      href: '#/dashboard',
      icon: 'briefcase',
      idx: '03',
      title: 'لوحة مقدم الخدمة',
      sub: 'أضف خدماتك، حدّد أوقات العمل المتاحة، وتابع طلبات العملاء أولًا بأول.',
      cta: 'إدارة نشاطي'
    }));

  mount(masthead, bento);
}

function renderNav() {
  if (!isAuthed()) { $nav.replaceChildren(); return; }
  const s = Api.session.get();
  const seg = location.hash.replace(/^#\//, '').split('/')[0] || 'home';
  const here = seg === 'provider' ? 'providers' : seg;
  const link = (href, label, key) => el('a', {
    href,
    class: 'navlink',
    text: label,
    'aria-current': here === key ? 'page' : null
  });
  const whoName = (s.user && s.user.name) || '';
  $nav.replaceChildren(
    link('#/home', 'الرئيسية', 'home'),
    link('#/providers', 'مقدمو الخدمة', 'providers'),
    link('#/bookings', 'حجوزاتي', 'bookings'),
    link('#/dashboard', 'لوحة مقدم الخدمة', 'dashboard'),
    whoName ? el('span', { class: 'who' }, ico('user'), whoName) : null,
    el('button', {
      type: 'button',
      class: 'btn ghost logout-btn',
      onclick: () => { Api.session.clear(); location.hash = '#/login'; }
    }, ico('logout'), 'خروج'));
}

function render() {
  const parts = location.hash.replace(/^#\/?/, '').split('/');
  const name = parts[0] || 'home';
  const authed = isAuthed();
  const guest = name === 'login' || name === 'register';
  if (!authed && !guest) { location.hash = '#/login'; return; }
  if (authed && guest) { location.hash = '#/home'; return; }
  renderNav();
  if (guest) authView(name);
  else if (name === 'providers') providersView();
  else if (name === 'provider' && /^\d+$/.test(parts[1] || '')) providerView(Number(parts[1]));
  else if (name === 'bookings') bookingsView();
  else if (name === 'dashboard') dashboardView();
  else if (name === 'home') homeView();
  else { location.hash = '#/home'; return; }
  window.scrollTo(0, 0);
}

window.addEventListener('hashchange', render);
render();