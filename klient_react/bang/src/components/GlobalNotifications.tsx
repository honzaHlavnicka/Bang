import { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import { subscribe,  } from "../modules/notify";
import type {NotificationMessage} from "../modules/notify";
import css from "../styles/notification.module.css";
export default function GlobalNotifications() {
  const [messages, setMessages] = useState<NotificationMessage[]>([]);

  useEffect(() => {
    const unsubscribe = subscribe((m) => setMessages(m));
    return unsubscribe;
  }, []);

  return createPortal(
    (<div>
        {messages.map((m) => {return (
        <div
          key={m.id}
          className={css.container}
        >
          {m.text}
        </div>)})}
    </div>),
    document.body
  );
}
