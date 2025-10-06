import { useState } from "react";
import { DialogContext, type DialogState, type DialogType } from "./DialogContext";

export function DialogProvider({ children }: { children: React.ReactNode }) {
    const [dialog, setDialog] = useState<DialogState>({ type: null, data: null });

    const openDialog = (type: DialogType, data: any = null) => {
        //TODO: povolit čekání na uzavření původního dialogu
        setDialog({ type, data });
    };

    const closeDialog = () => {
        //TODO: zkontrolovat, zda byla prvedena potřebná akce.
        setDialog({ type: null, data: null });
    };

    return (
        <DialogContext.Provider value={{ dialog, openDialog, closeDialog }}>
            {children}
        </DialogContext.Provider>
    );
}