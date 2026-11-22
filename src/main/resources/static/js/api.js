const Api = (() => {
  const KEY = 'bk_session';
  const session = {
    get() { try { return JSON.parse(localStorage.getItem(KEY)); } catch { return null; } },
    set(v) { localStorage.setItem(KEY, JSON.stringify(v)); },
    clear() { localStorage.removeItem(KEY); }
  };
  const MSG = {
    INVALID_CREDENTIALS: 'البريد الإلكتروني أو كلمة المرور غير صحيحة',
    EMAIL_ALREADY_EXISTS: 'هذا البريد الإلكتروني مسجّل بالفعل',
    PROVIDER_ALREADY_EXISTS: 'لديك حساب مقدم خدمة بالفعل',
    VALIDATION_FAILED: 'تأكد من صحة البيانات المدخلة',
    MALFORMED_REQUEST: 'الطلب غير صالح',
    INVALID_REQUEST: 'الطلب غير صالح',
    UNAUTHORIZED: 'انتهت الجلسة، سجّل الدخول من جديد',
    FORBIDDEN: 'غير مسموح لك بهذه العملية',
    NOT_FOUND: 'العنصر المطلوب غير موجود',
    AVAILABILITY_OVERLAP: 'هذا الوقت يتقاطع مع وقت متاح موجود',
    SLOT_NOT_AVAILABLE: 'هذا الموعد خارج الأوقات المتاحة',
    BOOKING_SLOT_TAKEN: 'هذا الموعد اتحجز للتو، اختر موعدًا آخر',
    SERVICE_INACTIVE: 'هذه الخدمة موقوفة حاليًا',
    PROVIDER_NOT_ACCEPTING_BOOKINGS: 'مقدم الخدمة لا يقبل حجوزات حاليًا',
    INTERNAL_ERROR: 'حدث خطأ غير متوقع، حاول مرة أخرى',
    GENERIC: 'تعذّر تنفيذ العملية',
    NETWORK: 'تعذّر الاتصال بالخادم'
  };
  async function request(method, path, body) {
    const s = session.get();
    const headers = { 'Content-Type': 'application/json' };
    if (s && !path.startsWith('/auth/')) headers.Authorization = 'Bearer ' + s.accessToken;
    let res;
    try {
      res = await fetch('/api/v1' + path, { method, headers, body: body ? JSON.stringify(body) : undefined });
    } catch {
      return { ok: false, status: 0, error: { code: 'NETWORK', message: MSG.NETWORK, fieldErrors: [] } };
    }
    let json = null;
    try { json = await res.json(); } catch { json = null; }
    if (res.ok && json) return { ok: true, status: res.status, data: json.data };
    const err = (json && json.error) || { code: 'INTERNAL_ERROR', fieldErrors: [] };
    if (res.status === 401 && s && !path.startsWith('/auth/')) { session.clear(); location.hash = '#/login'; }
    const fallback = res.status >= 500 ? MSG.INTERNAL_ERROR : MSG.GENERIC;
    return { ok: false, status: res.status, error: { ...err, message: MSG[err.code] || fallback } };
  }
  return { session, get: p => request('GET', p), post: (p, b) => request('POST', p, b) };
})();