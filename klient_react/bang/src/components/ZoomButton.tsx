import { useEffect } from "react";
import { useZoom } from "../../src/modules/ZoomContext";
import globalCSS from "../styles/global.module.css";
export default function ZoomToggleButton() {
  const { isZoomMode, toggleZoomMode } = useZoom();
  
  useEffect(() => {
    document.body.style.cursor = isZoomMode ? "zoom-in" : "auto";
    return () => {
      document.body.style.cursor = "auto";
    };
  }, [isZoomMode]);


  return (
    <button
    className={globalCSS.button}
    title={isZoomMode ? "Lupa je zapnutá, kliknutím vypnete" : "Zapnout lupu, kliknutím vypnete"}
    onClick={toggleZoomMode}
    >
      
      {isZoomMode ? <>🔎lupa zapnuta🔍</> : "🔎Zapnout lupu"}
    </button>
  );
}