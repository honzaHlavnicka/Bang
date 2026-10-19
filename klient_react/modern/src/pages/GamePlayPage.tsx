import React from "react";
import { PlayersArea } from "../components/PlayersArea";
import { BoardTable } from "../components/BoardTable";
import { MyStatus } from "../components/MyStatus";
import { HandCards } from "../components/HandCards";
import { DragProvider } from "../components/DragContext";

export const GamePlayPage: React.FC = () => {
  return (
    <DragProvider>
      <div className="min-h-screen h-[100dvh] flex flex-col justify-between bg-[#161311] text-[#f5ede3] overflow-hidden select-none relative">
        {/* 1. Horní část: Ostatní hráči */}
        <header className="shrink-0 pt-1.5 sm:pt-2 px-2 z-10">
          <PlayersArea />
        </header>

        {/* 2. Střední část: Herní stůl (balíčky, oheň, zprávy, tlačítka) */}
        <main className="flex-1 flex flex-col items-center justify-center min-h-0 overflow-y-auto px-2">
          <BoardTable />
        </main>

        {/* 3. Spodní část: Můj stav a karty v ruce */}
        <footer className="shrink-0 flex flex-col z-20">
          <MyStatus />
          <HandCards />
        </footer>
      </div>
    </DragProvider>
  );
};

export default GamePlayPage;
