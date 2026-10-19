import React from "react";
import { themeClasses } from "./tokens";

export type ButtonVariant = "primary" | "secondary" | "sage" | "terracotta" | "ghost";
export type ButtonSize = "sm" | "md" | "lg";

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  icon?: React.ReactNode;
  shortcutHint?: string;
  loading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = "secondary",
  size = "md",
  icon,
  shortcutHint,
  loading = false,
  className = "",
  disabled,
  ...props
}) => {
  const variantClass = {
    primary: themeClasses.buttonPrimary,
    secondary: themeClasses.buttonSecondary,
    sage: themeClasses.buttonSage,
    terracotta: themeClasses.buttonTerracotta,
    ghost: themeClasses.buttonGhost,
  }[variant];

  const sizeClass = {
    sm: "px-2.5 py-1 text-xs rounded-lg gap-1.5",
    md: "px-3.5 py-2 text-sm rounded-xl gap-2",
    lg: "px-5 py-2.5 text-base rounded-xl gap-2.5",
  }[size];

  return (
    <button
      className={`${variantClass} ${sizeClass} ${className}`}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? (
        <span className="inline-block w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin" />
      ) : (
        icon && <span className="shrink-0">{icon}</span>
      )}
      <span>{children}</span>
      {shortcutHint && (
        <kbd className="ml-1 px-1.5 py-0.5 text-[10px] uppercase font-mono tracking-wider bg-black/25 text-[#f5ede3]/70 rounded border border-white/10">
          {shortcutHint}
        </kbd>
      )}
    </button>
  );
};

export default Button;
