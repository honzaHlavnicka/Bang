import React from "react";

export const CardsEmblem: React.FC<{ className?: string }> = ({ className = "w-10 h-10" }) => {
  return (
    <svg
      viewBox="0 0 64 64"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={`${className} shrink-0 drop-shadow-md`}
    >
      {/* Zadní karta (nakloněná doleva) */}
      <rect
        x="12"
        y="14"
        width="28"
        height="40"
        rx="4"
        transform="rotate(-14 26 34)"
        fill="#241b16"
        stroke="#594336"
        strokeWidth="2"
      />
      {/* Vzor zadní karty */}
      <rect
        x="15"
        y="17"
        width="22"
        height="34"
        rx="2.5"
        transform="rotate(-14 26 34)"
        fill="#362921"
        opacity="0.8"
      />
      {/* Pika / symbol na zadní kartě */}
      <path
        d="M23 31 C23 27 26 24 26 24 C26 24 29 27 29 31 C29 33 27.5 34 26 34 C24.5 34 23 33 23 31 Z M25.5 33 L24.5 36 L27.5 36 L26.5 33 Z"
        fill="#d97706"
        transform="rotate(-14 26 34)"
      />

      {/* Přední karta (nakloněná doprava) */}
      <rect
        x="24"
        y="10"
        width="28"
        height="40"
        rx="4"
        transform="rotate(8 38 30)"
        fill="#1e1713"
        stroke="#d97706"
        strokeWidth="2.5"
      />
      {/* Vnitřní ozdobný rámeček */}
      <rect
        x="27"
        y="13"
        width="22"
        height="34"
        rx="2.5"
        transform="rotate(8 38 30)"
        fill="#2b201a"
      />
      {/* Červené / jantarové srdce / symbol ve středu */}
      <path
        d="M38 27 C36 24 33 26 33 28.5 C33 31.5 38 35.5 38 35.5 C38 35.5 43 31.5 43 28.5 C43 26 40 24 38 27 Z"
        fill="#ef4444"
        transform="rotate(8 38 30)"
      />
      {/* Rohové písmeno 'A' */}
      <text
        x="29"
        y="21"
        fill="#f5ede3"
        fontSize="7"
        fontWeight="bold"
        fontFamily="serif"
        transform="rotate(8 38 30)"
      >
        A
      </text>
    </svg>
  );
};

export default CardsEmblem;
