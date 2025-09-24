import { useEffect } from "react";
import { useZoom } from "../../src/modules/ZoomContext";

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
      onClick={toggleZoomMode}
    >
      
      {isZoomMode ? "Vypnout lupu" : "Zapnout lupu"}
    </button>
  );
}