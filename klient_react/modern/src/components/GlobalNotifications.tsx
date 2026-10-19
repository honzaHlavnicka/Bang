import React, { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import { subscribe, type NotificationMessage } from "@bang/core";
import { AnimatePresence, motion } from "framer-motion";

export const GlobalNotifications: React.FC = () => {
  const [messages, setMessages] = useState<NotificationMessage[]>([]);

  useEffect(() => {
    const unsubscribe = subscribe((msgs) => setMessages(msgs));
    return unsubscribe;
  }, []);

  if (typeof document === "undefined") return null;

  return createPortal(
    <div className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-[99999] pointer-events-none flex flex-col items-center gap-3 px-4 select-none max-w-[90vw]">
      <AnimatePresence>
        {messages.map((m) => (
          <motion.div
            key={m.id}
            initial={{ opacity: 0, scale: 0.3 }}
            animate={{ opacity: 1, scale: [0.3, 1.15, 1.0] }}
            exit={{ opacity: 0, scale: 1.4, transition: { duration: 0.25 } }}
            transition={{ duration: 0.3, ease: "easeOut" }}
            className="px-6 py-3.5 rounded-2xl bg-[#1c1511]/95 border-2 border-amber-400 text-amber-200 text-xl sm:text-2xl md:text-3xl font-extrabold shadow-[0_0_50px_rgba(245,158,11,0.6)] backdrop-blur-md tracking-wide text-center"
          >
            {m.text}
          </motion.div>
        ))}
      </AnimatePresence>
    </div>,
    document.body
  );
};

export default GlobalNotifications;
