import { createContext, useContext, useState } from "react";
import type { CardType } from "./GameContext";

type dialogBase = {
    notCloasable?: boolean;
    dialogHeader?: string;
}
type selectCardDialog = {
    type:"SELECT_CARD",
    data:{
        cards:Array<CardType>;
        min:number;
        max:number;
    }
}

type selectPlayerDialog = {
    type:"SELECT_PLAYER",
    data:{
        players:Array<{id:number,name:string}> //TODO: předpřipravený typ
    }
}

type confirmActionDialog = {
    type:"CONFIRM_ACTION",
    data:{
        actions:Array<{id:number,name:string}>
    }
}

export type DialogState = (dialogBase & selectCardDialog) | (dialogBase & selectPlayerDialog) | (dialogBase & confirmActionDialog);

export type DialogContextType = {
    dialog: DialogState;
    openDialog: (type: DialogType, data?: any) => void;
    closeDialog: () => void;
};

export const DialogContext = createContext<DialogContextType | null>(null);



export function useDialog() {
    const context = useContext(DialogContext);
    if (!context) throw new Error("useDialog must be used within a DialogProvider");
    return context;
}