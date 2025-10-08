import { useState } from "react";
import { DialogContext, type DialogState } from "./DialogContext";

export function DialogProvider({ children }: { children: React.ReactNode }) {
    const [dialog, setDialog] = useState<DialogState>(null);

    const openDialog = (dialog:DialogState) => {
        //TODO: povolit čekání na uzavření původního dialogu
        setDialog(dialog);
    };

    const closeDialog = () => {
        //TODO: zkontrolovat, zda byla prvedena potřebná akce.
        setDialog(null);
    };

    return (
        <DialogContext.Provider value={{ dialog, openDialog, closeDialog }}>
            {children}
        </DialogContext.Provider>
    );
}