import React, { useEffect } from "react";
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

  return (
    <div
      role="dialog"
      aria-modal="true"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-[2px] animate-in fade-in duration-150"
      onClick={(e) => {
        if (e.target === e.currentTarget && !notClosable) {
          onClose?.();
        }
      }}
    >
      <div
        className={`w-full ${maxWidthClass} bg-[#241d18] border border-[#423830] rounded-2xl shadow-2xl p-5 md:p-6 text-[#f5ede3] relative overflow-hidden`}
      >
        {/* Hlavička modalu */}
        {(title || !notClosable) && (
          <div className="flex items-start justify-between gap-4 mb-4 pb-3 border-b border-[#382f28]">
            <div>
              {title && <h3 className="text-lg font-semibold text-[#f5ede3]">{title}</h3>}
              {subtitle && <p className="text-xs text-[#b5a596] mt-0.5">{subtitle}</p>}
            </div>
            {!notClosable && onClose && (
              <button
                onClick={onClose}
                className="p-1 text-[#b5a596] hover:text-[#f5ede3] hover:bg-[#322822] rounded-lg transition-colors cursor-pointer"
                aria-label="Zavřít dialog (Esc)"
                title="Zavřít dialog (Esc)"
              >
                <X className="w-5 h-5" />
              </button>
            )}
          </div>
        )}

        {/* Tělo modalu */}
        <div className="space-y-4 max-h-[80vh] overflow-y-auto pr-1">{children}</div>
      </div>
    </div>
  );
};

export default ModalFrame;
