import React, { useEffect } from "react";
import { createPortal } from "react-dom";
import { X } from "lucide-react";

export interface ModalFrameProps {
  isOpen: boolean;
  onClose?: () => void;
  title?: React.ReactNode;
  subtitle?: React.ReactNode;
  children: React.ReactNode;
  maxWidth?: "sm" | "md" | "lg" | "xl";
  notClosable?: boolean;
}

export const ModalFrame: React.FC<ModalFrameProps> = ({
  isOpen,
  onClose,
  title,
  subtitle,
  children,
  maxWidth = "md",
  notClosable = false,
}) => {
  // Zavření na klávesu Escape
  useEffect(() => {
    if (!isOpen || notClosable) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        onClose?.();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, notClosable, onClose]);

  if (!isOpen) return null;

  const maxWidthClass = {
    sm: "max-w-sm",
    md: "max-w-md",
    lg: "max-w-lg",
    xl: "max-w-2xl",
  }[maxWidth];

  const modalContent = (
    <div
      role="dialog"
      aria-modal="true"
      className="fixed inset-0 z-[100] flex items-center justify-center p-3 sm:p-4 bg-black/80 backdrop-blur-[2px] animate-in fade-in duration-150"
      onClick={(e) => {
        if (e.target === e.currentTarget && !notClosable) {
          onClose?.();
        }
      }}
    >
      <div
        className={`w-full ${maxWidthClass} max-h-[90dvh] flex flex-col bg-[#211710] border border-[#3e3025] rounded-2xl shadow-2xl p-4 sm:p-5 md:p-6 text-[#f5ede3] relative overflow-hidden`}
      >
        {/* Hlavička modalu */}
        {(title || !notClosable) && (
          <div className="flex items-start justify-between gap-4 mb-3 sm:mb-4 pb-2.5 sm:pb-3 border-b border-[#382b21] shrink-0">
            <div>
              {title && <h3 className="text-base sm:text-lg font-bold text-[#f7eee4] tracking-tight">{title}</h3>}
              {subtitle && <p className="text-xs text-[#bcae9f] mt-0.5">{subtitle}</p>}
            </div>
            {!notClosable && onClose && (
              <button
                onClick={onClose}
                className="p-1.5 text-[#bfae9e] hover:text-[#fff4e8] hover:bg-[#2c2017] rounded-lg transition-colors cursor-pointer"
                aria-label="Zavřít dialog (Esc)"
                title="Zavřít dialog (Esc)"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>
        )}

        {/* Tělo modalu */}
        <div className="space-y-3 sm:space-y-4 overflow-y-auto pr-1 flex-1 min-h-0">{children}</div>
      </div>
    </div>
  );

  if (typeof document !== "undefined") {
    return createPortal(modalContent, document.body);
  }
  return modalContent;
};

export default ModalFrame;
