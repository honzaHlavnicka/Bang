import React from "react";
import { CardView } from "../design-system";
import { Layers } from "lucide-react";

interface DiscardDeckProps {
  images: string[];
  isDraggingCard?: boolean;
  isActive?: boolean;
}

// Předpočítané přirozené rotace a posuny pro efekt reálného balíčku
const stackTransforms = [
  { rot: -6, dx: -3, dy: 2 },
  { rot: 4, dx: 2, dy: -3 },
  { rot: -3, dx: -1, dy: 1 },
  { rot: 5, dx: 3, dy: 2 },
  { rot: -2, dx: 1, dy: -2 },
  { rot: 6, dx: -2, dy: 3 },
  { rot: 0, dx: 0, dy: 0 },
];

export const DiscardDeck: React.FC<DiscardDeckProps> = ({
  images = [],
  isDraggingCard = false,
  isActive = false,
}) => {
  // Posledních až 5 karet pro vytvoření hloubky
  const recentCards = images.slice(-5);
  const topCard = recentCards.length > 0 ? recentCards[recentCards.length - 1] : null;

  return (
    <div
      id="table"
      data-dropzone="table"
      className={`relative w-16 sm:w-22 md:w-28 aspect-[660/1035] rounded-xl flex items-center justify-center transition-all duration-150 select-none ${
        isActive
          ? "ring-2 ring-amber-400 bg-amber-500/20 scale-105 shadow-[0_0_15px_rgba(245,158,11,0.4)]"
          : isDraggingCard
          ? "ring-1 ring-amber-400/60 scale-[1.02]"
          : "hover:ring-1 hover:ring-amber-500/30"
      }`}
    >
      {recentCards.length > 0 ? (
        <div className="relative w-full h-full flex items-center justify-center pointer-events-none">
          {recentCards.map((cardName, idx) => {
            const isTop = idx === recentCards.length - 1;
            const tf = stackTransforms[idx % stackTransforms.length];
            const rot = isTop ? 0 : tf.rot;
            const dx = isTop ? 0 : tf.dx;
            const dy = isTop ? 0 : tf.dy;

            return (
              <div
                key={`${cardName}_${idx}`}
                style={{
                  transform: `translate(${dx}px, ${dy}px) rotate(${rot}deg)`,
                  zIndex: idx + 1,
                }}
                className="absolute transition-transform duration-200"
              >
                <CardView
                  image={cardName}
                  size="md"
                  className={isTop ? "shadow-xl" : "shadow-md"}
                />
              </div>
            );
          })}
        </div>
      ) : (
        <div className="w-full h-full border border-dashed border-[#382f28] rounded-[6px] flex flex-col items-center justify-center text-[#7a6c60] p-2 text-center bg-[#1c1714]">
          <Layers className="w-6 h-6 mb-1 opacity-40" />
          <span className="text-[11px] font-medium text-[#b5a596]">Odhazovací balíček</span>
          <span className="text-[9px] text-[#7a6c60] mt-0.5">Sem se odhazují karty</span>
        </div>
      )}
    </div>
  );
};

export default DiscardDeck;
