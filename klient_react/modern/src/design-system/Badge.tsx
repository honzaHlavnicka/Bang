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
    amber: "bg-[#342114] text-[#f8d7a3] border border-[#7a4e28] shadow-xs font-semibold",
    sage: "bg-[#182619] text-[#a9e4ba] border border-[#396340] shadow-xs font-semibold",
    terracotta: "bg-[#341612] text-[#f8aba2] border border-[#7a2c22] shadow-xs font-semibold",
    neutral: "bg-[#231812] text-[#ede3d7] border border-[#4c3728] shadow-xs font-medium",
    outline: "bg-[#170f0a] border border-[#4a3627] text-[#c4b5a5]",
  }[variant];

  const dotColor = {
    amber: "bg-amber-400 shadow-[0_0_4px_rgba(251,191,36,0.6)]",
    sage: "bg-emerald-400 shadow-[0_0_4px_rgba(52,211,153,0.6)]",
    terracotta: "bg-red-400 shadow-[0_0_4px_rgba(248,113,113,0.6)]",
    neutral: "bg-[#bcae9f]",
    outline: "bg-[#7d6e60]",
  }[variant];

  const sizeClass = size === "sm" ? "px-2 py-0.5 text-[11px] rounded-md" : "px-2.5 py-1 text-xs rounded-lg";

  return (
    <span
      className={`inline-flex items-center gap-1.5 tracking-wide ${variantClass} ${sizeClass} ${className}`}
    >
      {dot && <span className={`w-1.5 h-1.5 rounded-full ${dotColor}`} />}
      {children}
    </span>
  );
};

export default Badge;
