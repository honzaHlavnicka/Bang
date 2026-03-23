import { useState } from "react";
import { DialogContext, type DialogState } from "./DialogContext";

export function DialogProvider({ children }: { children: React.ReactNode }) {
    const [dialogQueue, setDialogQueue] = useState<Array<DialogState>>([]);

    const openDialog = (dialogData: DialogState) => {
        setDialogQueue((prev) => [...prev, dialogData]);
    };

    const closeDialog = () => {
        setDialogQueue((prev) => prev.slice(1));
    };

    const currentDialog = dialogQueue.length > 0 ? dialogQueue[0] : null;

    return (
        <DialogContext.Provider value={{ dialog: currentDialog, openDialog, closeDialog }}>
            {children}
        </DialogContext.Provider>
    );
}