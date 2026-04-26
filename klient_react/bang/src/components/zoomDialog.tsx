import { createPortal } from "react-dom";
import { useZoom } from "../modules/ZoomContext";
import css from "../styles/zoomDialog.module.css";
import DarkModeSwitch from "./DarkModeSwitch";
import { useTranslation } from "react-i18next";

export default function ZoomDialog() {
    const { zoomedCard, setZoomedCard , toggleZoomMode} = useZoom();
    const isOpen = zoomedCard !== null;
    const {t} = useTranslation();

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
                        onClick={toggleZoomMode}
                        src={zoomedCard}
                        alt="Zoomed Card"
                        className={css.image}
                    />
                )}
            </div>
        </div>,
        document.body
    );
}