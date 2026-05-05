import { createPortal } from "react-dom";
import { useZoom } from "../modules/ZoomContext";
import css from "../styles/zoomDialog.module.css";
import DarkModeSwitch from "./DarkModeSwitch";
import { useTranslation } from "react-i18next";
import { usePostHog } from "@posthog/react";
import { useEffect } from "react";

export default function ZoomDialog() {
    const { zoomedCard, setZoomedCard , toggleZoomMode, isZoomMode} = useZoom();
    const isOpen = zoomedCard !== null;
    const {t} = useTranslation();
    const posthog = usePostHog();

    useEffect(() => {
        if (isOpen) {
            posthog?.capture('card_zoomed', { image: zoomedCard });
        }
    }, [isOpen, zoomedCard, posthog]);

    const handleToggleInDialog = () => {
        const nextMode = !isZoomMode;
        posthog?.capture('magnifier_toggled', { enabled: nextMode, location: 'zoom_dialog' });
        toggleZoomMode();
    };

    return createPortal(
        <div
            className={`${css.backdrop} ${isOpen ? css.open : css.hidden}`}
            onClick={() => setZoomedCard(null)}
        >

            <div className={css.modal} onClick={e => e.stopPropagation()}>
                <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>

                <div className={css.header}>
                    {t("Přiblížená karta (kliknutím mimo kartu zavřít, kliknutím na kartu vypnou lupu)")}   
                </div>
                {zoomedCard && (
                    <img
                        style={{cursor:"zoom-out"}}
                        onClick={handleToggleInDialog}
                        src={zoomedCard}
                        alt={t("zoomed_card_alt")}
                        className={css.image}
                    />
                )}
            </div>
        </div>,
        document.body
    );
}