import React from "react";

export type BadgeVariant = "amber" | "sage" | "terracotta" | "neutral" | "outline";

export interface BadgeProps {
  children: React.ReactNode;
  variant?: BadgeVariant;
  size?: "sm" | "md";
  dot?: boolean;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = "neutral",
  size = "sm",
  dot = false,
  className = "",
}) => {
  const variantClass = {
    amber: "bg-amber-500/15 text-amber-300 border border-amber-500/30",
    sage: "bg-emerald-500/15 text-emerald-300 border border-emerald-500/30",
    terracotta: "bg-red-500/15 text-red-300 border border-red-500/30",
    neutral: "bg-[#2d2520] text-[#f5ede3] border border-[#423830]",
    outline: "border border-[#423830] text-[#b5a596]",
  }[variant];

  const dotColor = {
    amber: "bg-amber-400",
    sage: "bg-emerald-400",
    terracotta: "bg-red-400",
    neutral: "bg-[#b5a596]",
    outline: "bg-[#7a6c60]",
  }[variant];

  const sizeClass = size === "sm" ? "px-2 py-0.5 text-[11px]" : "px-2.5 py-1 text-xs";

  return (
    <span
      className={`inline-flex items-center gap-1.5 font-medium rounded-md tracking-wide ${variantClass} ${sizeClass} ${className}`}
    >
      {dot && <span className={`w-1.5 h-1.5 rounded-full ${dotColor}`} />}
      {children}
    </span>
  );
};

export default Badge;
