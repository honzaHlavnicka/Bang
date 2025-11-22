export type NotificationMessage = { id: number; text: string };

let listeners: ((messages: NotificationMessage[]) => void)[] = [];
let messages: NotificationMessage[] = [];

export function notify(text: string) {
  const msg = { id: Date.now(), text };
  messages.push(msg);
  listeners.forEach((l) => l([...messages]));

  // auto-remove po 1.2s
  setTimeout(() => {
    messages = messages.filter((m) => m.id !== msg.id);
    listeners.forEach((l) => l([...messages]));
  }, 1200);
}

export function subscribe(
  listener: (messages: NotificationMessage[]) => void
): () => void {
  listeners.push(listener);
  return () => {
    listeners = listeners.filter((l) => l !== listener);
  };
}
