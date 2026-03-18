import { createContext, useContext } from "react";
import type { CardType } from "./GameContext";

type dialogBase = {
    notClosable?: boolean;
    dialogHeader?: string;
}
type selectCardDialog = {
    type:"SELECT_CARD",
    data:{
        cards:Array<CardType>;
        min:number;
        max:number;
    }
    callback?:(selectedCards:Array<number>)=>void;
}

type selectPlayerDialog = {
    type:"SELECT_PLAYER",
    data:{
        players:Array<{id:number,name:string}>,
        min:number,
        max:number
    }
    callback:(selectedPlayers:number[])=>void;
}

type confirmActionDialog = {
    type:"CONFIRM_ACTION",
    data:{
        actions:Array<{id:number,name:string}>
    }
    callback?:(selectedAction:number)=>void;
}
type infoDialog = {
    type:"INFO",
    data:{
        header?:string;
        message:string;
        image?:string;
    }
}

type confirmDialog = {
    type:"CONFIRM",
    data:{
        message?:string,
        title?:string,
    },
    callback?:(confirmed:boolean)=>void;
}

type textDialog = {
    type:"TEXT",
    data:{
        title?:string;
        placeholder?:string;
        buttonText?:string;
        defaultValue?:string;
    },
    callback?:(text:string)=>void;
}

export type DialogState = (dialogBase & selectCardDialog) | (dialogBase & selectPlayerDialog) | (dialogBase & confirmActionDialog) | (dialogBase & infoDialog) | (dialogBase & confirmDialog) | (dialogBase & textDialog) | null;

export type DialogContextType = {
    dialog: DialogState;
    openDialog: (dialog:DialogState) => void;
    closeDialog: () => void;

};

export const DialogContext = createContext<DialogContextType | null>(null);



export function useDialog() {
    const context = useContext(DialogContext);
    if (!context) throw new Error("useDialog must be used within a DialogProvider");
    return context;
}