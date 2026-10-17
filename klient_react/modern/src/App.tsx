import { useGame, useDialog } from '@bang/core';

export default function App() {
  const { gameState, isConnected } = useGame();
  const { dialog, closeDialog } = useDialog();

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-2">Nový frontend</h1>
      <p className="text-sm text-gray-500 mb-4">
        Stav připojení: {isConnected ? 'Připojeno' : 'Odpojeno'} | Ve hře: {gameState.inGame ? 'Ano' : 'Ne'}
      </p>

      {/* Základní kostra připravená pro naprogramování nového vzhledu */}
      <div className="border border-dashed border-gray-300 dark:border-gray-700 rounded-xl p-8 text-center text-gray-500">
        Tady bude nová verse hry.
      </div>
    </div>
  );
}
