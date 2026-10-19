import React, { useState, useEffect, useRef } from "react";
import { motion } from "framer-motion";
import { Button } from "../design-system";
import { translateServerText } from "@bang/core";
import { Sparkles } from "lucide-react";

interface WheelOption {
  id: number;
  name: string;
  color: string;
}

interface LuckyWheelProps {
  options: WheelOption[];
  chosedOptionId: number;
  onConfirm: () => void;
  onSkip?: () => void;
}

export const LuckyWheel: React.FC<LuckyWheelProps> = ({
  options = [],
  chosedOptionId,
  onConfirm,
  onSkip,
}) => {
  const [spinning, setSpinning] = useState(false);
  const [rotation, setRotation] = useState(0);
  const [finished, setFinished] = useState(false);
  const hasStartedRef = useRef(false);

  const numOptions = Math.max(1, options.length);
  const sliceAngle = 360 / numOptions;

  // Hledání indexu výherní možnosti
  const chosenIndex = options.findIndex((o) => o.id === chosedOptionId);
  const safeIndex = chosenIndex >= 0 ? chosenIndex : 0;
  const chosenOption = options[safeIndex];

  useEffect(() => {
    // Spuštění točení s mírným zpožděním pro hladký náběh
    const timer = setTimeout(() => {
      hasStartedRef.current = true;
      setSpinning(true);
      // Ručička je nahoře (12 o'clock = 270°)
      // Střed vybraného segmentu v polárních souřadnicích
      const segmentMidAngle = (safeIndex + 0.5) * sliceAngle;
      const targetAngle = (270 - segmentMidAngle + 360) % 360;
      // 5 plných otáček + přesný cílový úhel
      const totalRotation = 360 * 5 + targetAngle;
      setRotation(totalRotation);
    }, 150);

    return () => clearTimeout(timer);
  }, [safeIndex, sliceAngle]);

  const handleAnimationComplete = () => {
    // Pouze po reálném roztočení a dotočení, nikoliv při mountu s rotací 0
    if (hasStartedRef.current && rotation > 0) {
      setSpinning(false);
      setFinished(true);
    }
  };

  // Klávesové zkratky: Enter / Mezerník po dotočení, Escape pro okamžité přeskočení
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        e.preventDefault();
        if (onSkip) {
          onSkip();
        } else {
          onConfirm();
        }
        return;
      }

      if (finished && (e.key === "Enter" || e.code === "Space")) {
        e.preventDefault();
        onConfirm();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [finished, onConfirm, onSkip]);

  // Vytvoření SVG cest pro kruhové výseče
  const radius = 100;
  const center = 110;

  const getCoordinatesForPercent = (percent: number) => {
    const x = center + radius * Math.cos(2 * Math.PI * percent);
    const y = center + radius * Math.sin(2 * Math.PI * percent);
    return [x, y];
  };

  return (
    <div className="flex flex-col items-center justify-center p-3 text-center select-none space-y-4">
      <div className="relative w-64 h-64 flex items-center justify-center">
        {/* Ručička kola (ukazatel nahoře) */}
        <div className="absolute -top-1 left-1/2 -translate-x-1/2 z-30 drop-shadow-md pointer-events-none">
          <div className="w-0 h-0 border-l-[10px] border-l-transparent border-r-[10px] border-r-transparent border-t-[18px] border-t-amber-400" />
        </div>

        {/* SVG kolo štěstí */}
        <svg viewBox="0 0 220 220" className="w-full h-full drop-shadow-xl overflow-visible">
          {/* Vnější ozdobný dřevěný lem (pevně ukotvený, bez chvění) */}
          <circle cx={center} cy={center} r={radius + 8} fill="#241a15" stroke="#4a372c" strokeWidth="4" />

          {/* Plynule rotující vnitřní výseče s přesným středem otáčení */}
          <motion.g
            animate={{ rotate: rotation }}
            transition={{
              duration: 6.5,
              ease: [0.15, 0.95, 0.25, 1], // Plynulé, realistické brzdění
            }}
            onAnimationComplete={handleAnimationComplete}
            style={{ transformOrigin: `${center}px ${center}px` }}
          >
            {/* Segmenty */}
            {options.map((opt, i) => {
              const startPercent = i / numOptions;
              const endPercent = (i + 1) / numOptions;
              const [startX, startY] = getCoordinatesForPercent(startPercent);
              const [endX, endY] = getCoordinatesForPercent(endPercent);
              const largeArcFlag = sliceAngle > 180 ? 1 : 0;
              const pathData = `M ${center} ${center} L ${startX} ${startY} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${endX} ${endY} Z`;

              // Úhel textu
              const textAngle = (startPercent + endPercent) * 180;
              const textRad = (textAngle * Math.PI) / 180;
              const textX = center + (radius * 0.65) * Math.cos(textRad);
              const textY = center + (radius * 0.65) * Math.sin(textRad);

              return (
                <g key={opt.id}>
                  <path
                    d={pathData}
                    fill={opt.color || (i % 2 === 0 ? "#b45309" : "#1e293b")}
                    stroke="#1a1410"
                    strokeWidth="2"
                  />
                  <text
                    x={textX}
                    y={textY}
                    fill="#ffffff"
                    fontSize="9"
                    fontWeight="bold"
                    textAnchor="middle"
                    dominantBaseline="middle"
                    transform={`rotate(${textAngle + 90}, ${textX}, ${textY})`}
                    className="drop-shadow-sm font-sans"
                  >
                    {translateServerText(opt.name)}
                  </text>
                </g>
              );
            })}
          </motion.g>

          {/* Středový zlatý čep */}
          <circle cx={center} cy={center} r="16" fill="#1c1613" stroke="#d97706" strokeWidth="3" />
          <circle cx={center} cy={center} r="6" fill="#f59e0b" />
        </svg>
      </div>

      {/* Stav a výsledek */}
      <div className="min-h-[48px] flex flex-col items-center justify-center">
        {finished ? (
          <div className="animate-in fade-in zoom-in-95 duration-200">
            <span className="text-[11px] text-amber-400 font-semibold uppercase tracking-wider block">
              Výsledek osudu:
            </span>
            <span className="text-lg font-bold text-[#f5ede3] flex items-center gap-1.5 justify-center mt-0.5">
              <Sparkles className="w-4 h-4 text-amber-400" />
              <span>{chosenOption ? translateServerText(chosenOption.name) : ""}</span>
            </span>
          </div>
        ) : (
          <span className="text-xs text-[#b5a596] animate-pulse">
            Kolo se točí...
          </span>
        )}
      </div>

      {/* Tlačítka: Pokračovat (po dotočení) nebo Přeskočit animaci (během točení) */}
      {finished ? (
        <Button
          variant="primary"
          size="md"
          className="w-full justify-center"
          autoFocus={true}
          shortcutHint="Enter"
          onClick={onConfirm}
        >
          Rozumím, pokračovat
        </Button>
      ) : (
        <Button
          variant="ghost"
          size="sm"
          className="w-full justify-center text-[#b5a596] hover:text-[#f5ede3] hover:bg-[#382f28]/60"
          onClick={onSkip || onConfirm}
        >
          Přeskočit animaci (Esc)
        </Button>
      )}
    </div>
  );
};

export default LuckyWheel;
