const $app = document.getElementById('app');
const $nav = document.getElementById('nav');
const CURRENCY = 'ج.م';

let viewToken = 0;
const beginView = () => ++viewToken;
const isStale = t => t !== viewToken;

const ICONS = {
  calendar: '<rect x="3" y="4" width="18" height="17" rx="3"/><path d="M8 2v4M16 2v4M3 10h18"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4 20c1.5-4 5-6 8-6s6.5 2 8 6"/>',
  briefcase: '<rect x="3" y="7" width="18" height="13" rx="2.5"/><path d="M8 7V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M3 12h18"/>',
  arrowLeft: '<path d="M19 12H5M11 6l-6 6 6 6"/>',
  arrowRight: '<path d="M5 12h14M13 6l6 6-6 6"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  x: '<path d="M18 6 6 18M6 6l12 12"/>',
  alert: '<path d="M12 3 2 20h20L12 3z"/><path d="M12 9v5M12 17h.01"/>',
  inbox: '<path d="M3 13V6a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v7"/><path d="M3 13h5l2 3h4l2-3h5v5a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-5z"/>',
  wrench: '<path d="M14.7 6.3a4 4 0 0 0-5.4 5.4L4 17l3 3 5.3-5.3a4 4 0 0 0 5.4-5.4l-2.6 2.6-2.4-2.4 2-2.2z"/>',
  logout: '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5M21 12H9"/>',
  sun: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
  moon: '<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/>'
};

function ico(name) {
  const s = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  s.setAttribute('viewBox', '0 0 24 24');
  s.setAttribute('fill', 'none');
  s.setAttribute('stroke', 'currentColor');
  s.setAttribute('stroke-width', '2');
  s.setAttribute('stroke-linecap', 'round');
  s.setAttribute('stroke-linejoin', 'round');
  s.setAttribute('aria-hidden', 'true');
  s.setAttribute('class', 'ico-svg');
  s.innerHTML = ICONS[name] || '';
  return s;
}

function avatarTone(name) {
  let h = 0;
  const str = String(name || '');
  for (let i = 0; i < str.length; i++) h = (h * 31 + str.charCodeAt(i)) % 5;
  return 'tone-' + Math.abs(h);
}

(function initThemeToggle() {
  const btn = document.getElementById('theme-toggle');
  if (!btn) return;
  const sync = () => {
    const dark = document.documentElement.dataset.theme === 'dark';
    btn.replaceChildren(ico(dark ? 'sun' : 'moon'));
    btn.title = dark ? 'التبديل للوضع النهاري' : 'التبديل للوضع الليلي';
  };
  btn.addEventListener('click', () => {
    const next = document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark';
    document.documentElement.dataset.theme = next;
    localStorage.setItem('bk_theme', next);
    sync();
  });
  sync();
})();

function el(tag, attrs, ...kids) {
  const n = document.createElement(tag);
  for (const [k, v] of Object.entries(attrs || {})) {
    if (v == null || v === false) continue;
    if (k === 'class') n.className = v;
    else if (k === 'text') n.textContent = v;
    else if (k.startsWith('on')) n.addEventListener(k.slice(2), v);
    else n.setAttribute(k, v === true ? '' : v);
  }
  const add = k => {
    if (k == null || k === false) return;
    if (Array.isArray(k)) k.forEach(add);
    else n.append(k instanceof Node ? k : document.createTextNode(String(k)));
  };
  kids.forEach(add);
  return n;
}

function mount(...nodes) { $app.replaceChildren(...nodes.filter(Boolean)); }

function toast(msg, type) {
  const t = el('div', { class: 'toast ' + (type || ''), text: msg });
  document.getElementById('toasts').appendChild(t);
  setTimeout(() => t.classList.add('out'), 3200);
  setTimeout(() => t.remove(), 3600);
}

const nf = new Intl.NumberFormat('ar-EG');
const dfWeekday = new Intl.DateTimeFormat('ar-EG', { weekday: 'long' });
const dfDayMonth = new Intl.DateTimeFormat('ar-EG', { day: 'numeric', month: 'long' });
const dfTime = new Intl.DateTimeFormat('ar-EG', { hour: 'numeric', minute: '2-digit' });
const fmtNum = n => nf.format(n);
const fmtPrice = p => fmtNum(Number(p)) + ' ' + CURRENCY;
const fmtWeekday = d => dfWeekday.format(d);
const fmtDayMonth = d => dfDayMonth.format(d);
const fmtTime = d => dfTime.format(d);
const fmtDateTime = d => fmtWeekday(d) + ' ' + fmtDayMonth(d) + ' · ' + fmtTime(d);

function fmtDuration(m) {
  m = Number(m) || 0;
  if (m < 60) return fmtNum(m) + ' دقيقة';
  const h = Math.floor(m / 60), r = m % 60;
  return fmtNum(h) + ' ساعة' + (r ? ' و' + fmtNum(r) + ' دقيقة' : '');
}

function toDate(v) { return new Date(typeof v === 'number' ? v * 1000 : v); }

function computeSlots(windows, minutes, now, taken) {
  const step = Number(minutes) * 60000;
  if (!(step > 0)) return [];
  const out = new Set();
  windows.forEach(w => {
    const s = toDate(w.startTime).getTime(), e = toDate(w.endTime).getTime();
    if (isNaN(s) || isNaN(e)) return;
    for (let t = s; t + step <= e; t += step) {
      if (t > now && !taken.some(x => t < x.end && t + step > x.start)) out.add(t);
    }
  });
  return [...out].sort((a, b) => a - b);
}

function groupByDay(slots) {
  const map = new Map();
  slots.forEach(ms => {
    const d = new Date(ms);
    const key = d.getFullYear() + '-' + d.getMonth() + '-' + d.getDate();
    if (!map.has(key)) map.set(key, { key, date: d, slots: [] });
    map.get(key).slots.push(ms);
  });
  return [...map.values()];
}

function openSheet(title, content) {
  const prevFocus = document.activeElement;
  const onKey = e => { if (e.key === 'Escape') close(); };
  function close() {
    document.removeEventListener('keydown', onKey);
    window.removeEventListener('hashchange', close);
    document.body.classList.remove('no-scroll');
    backdrop.remove();
    if (prevFocus && prevFocus.focus) prevFocus.focus();
  }
  const panel = el('div', { class: 'sheet', role: 'dialog', 'aria-modal': 'true', 'aria-label': title, tabindex: '-1' },
    el('div', { class: 'sheet-head' },
      el('h2', { text: title }),
      el('button', { type: 'button', class: 'icon-btn', 'aria-label': 'إغلاق', onclick: close }, ico('x'))),
    content);
  const backdrop = el('div', { class: 'backdrop', onclick: e => { if (e.target === backdrop) close(); } }, panel);
  document.body.appendChild(backdrop);
  document.body.classList.add('no-scroll');
  document.addEventListener('keydown', onKey);
  window.addEventListener('hashchange', close);
  panel.focus();
  return { close };
}