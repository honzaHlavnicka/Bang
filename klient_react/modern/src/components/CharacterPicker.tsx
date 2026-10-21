import React, { useEffect } from "react";
import { useGame, translateServerText } from "@bang/core";
import { CardView, Badge } from "../design-system";

export const CharacterPicker: React.FC = () => {
  const { gameState, chooseCharacter } = useGame();

  const characters = gameState.characters || [];

  // Klávesové zkratky 1 a 2 pro rychlý výběr
  useEffect(() => {
    if (gameState.character || characters.length === 0) return;

    const handleKeyDown = (e: KeyboardEvent) => {
      if (
        document.activeElement?.tagName === "INPUT" ||
        document.activeElement?.tagName === "TEXTAREA"
      ) {
        return;
      }

      if (e.key === "1" && characters.length >= 1) {
        e.preventDefault();
        chooseCharacter(characters[0].obrazek);
      } else if (e.key === "2" && characters.length >= 2) {
        e.preventDefault();
        chooseCharacter(characters[1].obrazek);
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [characters, gameState.character, chooseCharacter]);

  // Pokud postava už byla vybrána, nebo hra postavy nepodporuje, nezobrazujeme nic
  if (gameState.character) {
    return null;
  }
  if (!gameState.allowedUIElements.includes("POSTAVA")) {
    return null;
  }
  if (characters.length === 0) {
    return null;
  }

  return (
    <div className="p-4 sm:p-5 rounded-2xl bg-[#18100a] border border-[#3d2c20] shadow-xl space-y-3 text-center animate-in fade-in duration-200">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-bold text-[#f7eee4] tracking-tight">
          Vyber si svou postavu
        </h3>
        <Badge variant="amber" size="sm">
          Zvol kliknutím nebo klávesou 1 / 2
        </Badge>
      </div>

      <div className="flex flex-wrap items-center justify-center gap-4 sm:gap-6 py-2">
        {characters.map((char, index) => {
          const cardImg = `/img/karty/postavy/${char.obrazek}.png`;

          return (
            <div
              key={char.obrazek}
              onClick={() => chooseCharacter(char.obrazek)}
              className="relative cursor-pointer transition-all hover:scale-105 active:scale-95 group flex flex-col items-center max-w-[160px]"
            >
              <div className="relative">
                <CardView
                  image={cardImg}
                  name={char.jmeno}
                  size="md"
                  isPlayable={true}
                  className="border-amber-500/60 group-hover:border-amber-400 shadow-xl"
                  badge={
                    <span className="w-5 h-5 rounded-full bg-amber-500 text-stone-950 font-bold text-xs flex items-center justify-center shadow-md">
                      {index + 1}
                    </span>
                  }
                />
              </div>

              <div className="mt-2 text-center">
                <span className="font-bold text-xs text-[#f5ede3] block">
                  {char.jmeno}
                </span>
                <p className="text-[11px] text-[#bcae9f] leading-tight line-clamp-2 mt-0.5">
                  {char.popis}
                </p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CharacterPicker;
