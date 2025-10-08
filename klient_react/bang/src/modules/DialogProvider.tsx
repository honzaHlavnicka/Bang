import { useState } from "react";
import { DialogContext, type DialogState } from "./DialogContext";

export function DialogProvider({ children }: { children: React.ReactNode }) {
    const [dialog, setDialog] = useState<DialogState>(null);
    const [waitingDialogs, setWaitingDialogs] = useState<Array<DialogState>>([]);

    const openDialog = (dialogData:DialogState) => {
                            console.log(dialog)

        if(dialog != null){
            setWaitingDialogs([...waitingDialogs,dialogData]);
            return;
        }

        setDialog(dialogData);
    };

    const closeDialog = () => {
        //TODO: zkontrolovat, zda byla prvedena potřebná akce.
        setDialog(null);
        if(waitingDialogs.length > 0){
            const next = waitingDialogs[0];
            setWaitingDialogs(waitingDialogs.slice(1));
            setDialog(next);
        }
    };

    return (
        <DialogContext.Provider value={{ dialog, openDialog, closeDialog }}>
            {children}
        </DialogContext.Provider>
    );
}