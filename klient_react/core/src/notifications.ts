export interface NotificationHandler {
  (message: string, options?: any): void;
  success: (message: string, options?: any) => void;
  error: (message: string, options?: any) => void;
  loading: (message: string, options?: any) => void;
  dismiss: (id?: string) => void;
}

const defaultHandler: {
  (message: string, options?: any): void;
  success: (message: string, options?: any) => void;
  error: (message: string, options?: any) => void;
  loading: (message: string, options?: any) => void;
  dismiss: (id?: string) => void;
} = Object.assign(
  (msg: string, _opts?: any) => console.log('[Notification]', msg),
  {
    success: (msg: string, _opts?: any) => console.log('[Success]', msg),
    error: (msg: string, _opts?: any) => console.error('[Error]', msg),
    loading: (msg: string, _opts?: any) => console.log('[Loading]', msg),
    dismiss: (_id?: string) => {}
  }
);

let currentHandler = defaultHandler;

export function setNotificationHandler(handler: any) {
  if (!handler) {
    currentHandler = defaultHandler;
    return;
  }
  const baseFn = typeof handler === 'function' ? handler : (msg: string, opts?: any) => (handler.success || console.log)(msg, opts);
  currentHandler = Object.assign(baseFn, {
    success: handler.success || baseFn,
    error: handler.error || console.error,
    loading: handler.loading || console.log,
    dismiss: handler.dismiss || (() => {})
  });
}

export function getNotificationHandler(): NotificationHandler {
  return currentHandler;
}

export const notifications: NotificationHandler = Object.assign(
  (msg: string, opts?: any) => currentHandler(msg, opts),
  {
    success: (msg: string, opts?: any) => currentHandler.success(msg, opts),
    error: (msg: string, opts?: any) => currentHandler.error(msg, opts),
    loading: (msg: string, opts?: any) => currentHandler.loading(msg, opts),
    dismiss: (id?: string) => currentHandler.dismiss(id)
  }
);
