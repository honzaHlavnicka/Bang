import { createPortal } from "react-dom";
import css from "../styles/dialog.module.css";
import { useDialog } from "../modules/DialogContext";
import Card from "./Card";
import type { CardType } from "../modules/GameContext";

export default function Dialog() {
    const {closeDialog,dialog} = useDialog();

    const header = dialog.dialogHeader || dialog.type || "Dialog";

    let content: React.ReactNode;
    switch (dialog.type) {
        case "SELECT_CARD": 
            content = (
                <div style={{display:"flex",flexDirection:"row"}} >
                    {dialog.data.cards.map((val:CardType)=>(
                        <Card id={val.id} key={val.id} image={val.image}/>
                    ))}
                </div>
            );
            break;
    
        default:
            break;
    }


    return createPortal((
        <div className={css.dialogBackground} style={{cursor:(dialog.notCloasable ? "not-allowed" : "auto"),display:(dialog.type == null ? "none" : "flex")}} onClick={()=>closeDialog()}>
            <div className={css.dialogBox} onClick={e => e.stopPropagation()}>
                <div className={css.dialogHeader}>
                    {header}
                </div>
                <div className={css.dialogContent}>
                    {content}
                </div>
               
            </div>
        </div>
    ),document.body);
}