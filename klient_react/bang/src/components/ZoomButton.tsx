import { useEffect } from "react";
import { useZoom } from "../../src/modules/ZoomContext";
import globalCSS from "../styles/global.module.css";
import { useTranslation } from "react-i18next";
import { usePostHog } from "@posthog/react";

export default function ZoomToggleButton({style}:{style?:React.CSSProperties}) {
  const { isZoomMode, toggleZoomMode } = useZoom();
  const { t } = useTranslation();
  const posthog = usePostHog();

  useEffect(() => {
    document.body.style.cursor = isZoomMode ? "zoom-in" : "auto";
    return () => {
      document.body.style.cursor = "auto";
    };
  }, [isZoomMode]);

  const handleToggle = () => {
    const nextMode = !isZoomMode;
    posthog?.capture('magnifier_toggled', { enabled: nextMode });
    toggleZoomMode();
  };


  return (
    <button
    className={globalCSS.button}
    title={isZoomMode ? t("Lupa je zapnutá, kliknutím vypnete") : t("Zapnout lupu, kliknutím vypnete")}
    onClick={handleToggle}
    style={style}
    >

      {isZoomMode ? <>🔍{t("lupa zapnuta")}</> : <>🔎{t("Zapnout lupu")}</>}
    </button>
  );
}