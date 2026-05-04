import React, { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import css from "../styles/dialog.module.css";
import { useDialog } from "../modules/DialogContext";
import Card from "./Card";
import type { CardType } from "../modules/GameContext";
import ZoomToggleButton from "./ZoomButton";
import toast from "react-hot-toast";
import DarkModeSwitch from "./DarkModeSwitch";
import globalCSS from "../styles/global.module.css";
import LuckyWheel from "./LuckyWheel";
import { useTranslation } from "react-i18next";

export default function Dialog() {
    const { closeDialog, dialog} = useDialog();
    const [selected, setSelected] = useState<Array<number>>([]);
    const [text, setText] = useState("");
    const { t } = useTranslation();

    // Resetování stavů při změně dialogu
    useEffect(() => {
        setSelected([]);
        if (dialog?.type === "TEXT") {
            setText(dialog.data.defaultValue || "");
        } else {
            setText("");
        }
    }, [dialog]);

    // Obsluha Enter klávesy pro dialogy
    useEffect(() => {
        if (dialog == null) return;

        const handleKeyDown = (e: KeyboardEvent) => {
            if (dialog?.type !== "TEXT" && (document.activeElement?.tagName === "TEXTAREA" || 
                (document.activeElement?.tagName === "INPUT" && (document.activeElement as HTMLInputElement).type !== "text"))) {
                return;
            }

            if (e.key === "Enter" && dialog) {
                if (dialog.type !== "TEXT") {
                    e.preventDefault();
                }

                switch (dialog.type) {
                    case "INFO":
                        closeDialog();
                        break;
                    case "CONFIRM":
                        closeDialog();
                        if (dialog.callback) {
                            dialog.callback(true);
                        }
                        break;
                    case "TEXT":
                        break;
                    case "SELECT_PLAYER":
                        if (selected.length >= dialog.data.min) {
                            closeDialog();
                            dialog.callback(selected);
                            setSelected([]);
                        }
                        break;
                    case "SELECT_CARD":
                        if (selected.length >= dialog.data.min) {
                            closeDialog();
                            if (dialog.callback) {
                                dialog.callback(selected);
                            }
                            setSelected([]);
                        }
                        break;
                    case "CONFIRM_ACTION":
                        if (dialog.data.actions && dialog.data.actions.length > 0) {
                            closeDialog();
                            if (dialog.callback) {
                                dialog.callback(dialog.data.actions[0].id);
                            }
                        }
                        break;
                    case "LUCKY_WHEEL":
                        closeDialog();
                        if (dialog.callback) {
                            dialog.callback(dialog.data.chosedOptionId);
                        }
                        break;
                }
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [dialog, selected, text, closeDialog]);

    const header = dialog?.dialogHeader || (dialog?.type ? t(`dialog.${dialog.type}`) : t("dialog.Default"));

    const select = (id: number, max: number) => {
        if (selected.includes(id)){
            setSelected(selected.filter(v => v !== id));
        } else {
            if (selected.length >= max) {
                // Odstraní nejstarší výběr a přidá nový
                setSelected([...selected.slice(1), id]); 
            } else {
                setSelected([...selected, id]);
            }
        }
    };

    if (dialog == null) {
        return null; 
    }

    let content: React.ReactNode;
    
    switch (dialog.type) {
        case "SELECT_CARD": 
            content = (
                <div>
                    <div style={{display:"flex", flexDirection:"row", flexWrap:"wrap"}} >
                        {dialog.data.cards.map((val: CardType) => (
                            <div 
                                key={val.id} 
                                style={{background: (selected.includes(val.id) ? "brown" : "transparent"), borderRadius: 10, margin: 5, padding: 2}} 
                                onClick={() => select(val.id, dialog.data.max)} 
                            >
                                <Card id={val.id} image={"/img/karty/" + val.image + ".png"}/>
                            </div>
                        ))}
                    </div>
                    <div>{t("dialog.vybrano_z_max", { count: selected.length, max: dialog.data.max })}<br/>{t("dialog.min_max", { min: dialog.data.min, max: dialog.data.max })}</div>
                    <ZoomToggleButton style={{filter:"grayscale(70%)",scale:0.8}}/>
                    {(selected.length >= dialog.data.min) ? 
                        <button className={globalCSS.button} onClick={() => {
                            closeDialog();
                            if(dialog.callback){
                                dialog.callback(selected);
                            }
                            setSelected([]);
                        }}>{t("Potvrdit výběr")}</button>
                    : <div>{t("dialog.vyber_karty", { count: dialog.data.min - selected.length })}</div>}
                </div>
            );
            break;
            
        case "SELECT_PLAYER":
            content = (
                <div style={{display:"flex", flexDirection:"column", alignItems:"flex-start"}} >
                    {dialog.data.players.map((val: {id: number, name: string}) => (
                        <React.Fragment key={val.id}>
                            <label style={{background: (selected.includes(val.id) ? "grey" : "transparent"), borderRadius: 10, margin: 3, padding: 2, cursor: "pointer"}} >
                                <input 
                                    type={dialog.data.max === 1 ? "radio" : "checkbox"} 
                                    name="player" 
                                    value={val.id} 
                                    style={{marginRight: 10}} 
                                    onChange={() => select(val.id, dialog.data.max)} 
                                    checked={selected.includes(val.id)}
                                />
                                {val.name}
                            </label>
                            <br/>
                        </React.Fragment>
                    ))}
                    {selected.length >= dialog.data.min ? 
                        <button onClick={() => {
                            closeDialog();
                            dialog.callback(selected);
                            setSelected([]);
                        }} className={globalCSS.button}>{t("dialog.Potvrdit")}</button> 
                    : <div>{t("dialog.vyber_hracu", { count: dialog.data.min - selected.length })}</div>}
                </div>
            );
            break;
            
        case "CONFIRM_ACTION":
            content = (
                <div style={{display:"flex",flexDirection:"row",alignItems:"flex-start", flexWrap:"wrap", gap: 10}} >
                    {dialog.data.actions.map((val:{id:number,name:string})=>(   
                        <button className={globalCSS.button} key={val.id} onClick={() => {
                            closeDialog();
                            if(dialog.callback){
                                dialog.callback(val.id);
                            }
                        }}>
                            {val.name}
                        </button>
                    ))}
                </div>
            );
            break;
            
        case "INFO":
            content = (
                <div style={{display:"flex",flexDirection:"column",alignItems:"center", gap: 5}} >
                    {dialog.data.image ? <img src={dialog.data.image} alt={dialog.data.header} style={{width:"100%"}}/>: null}
                    {dialog.data.header ? <h2>{dialog.data.header}</h2> : null}
                    <p>{dialog.data.message}</p>
                    <button className={globalCSS.button} onClick={() => closeDialog()}>{t("dialog.OK")}</button>
                </div>
            );
            break;
        case "CONFIRM":
            content = (
                <div style={{display:"flex", flexDirection:"column", alignItems:"center", gap: 10}} >
                    <h2>{dialog.data.title || t("dialog.Potvrzení")}</h2>
                    <p>{dialog.data.message}</p>
                    <div style={{display:"flex", flexDirection:"row", gap: 10}}>
                        <button className={globalCSS.button} onClick={() => {
                            closeDialog();
                            if(dialog.callback) dialog.callback(true);
                        }}>{t("dialog.Ano")}</button>
                        <button className={globalCSS.button} onClick={() => {
                            closeDialog();
                            if(dialog.callback) dialog.callback(false);
                        }}>{t("dialog.Ne")}</button>
                    </div>
                </div>
            );
            break;
        case "TEXT":
            content = (
                <div style={{display:"flex",flexDirection:"column", alignItems:"center", gap: 10}} >
                    <h2>{dialog.data.title || t("dialog.Zadejte text")}</h2>
                    <input 
                        type="text" 
                        value={text} 
                        onChange={e=>setText(e.currentTarget.value)} 
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                closeDialog();
                                if(dialog.callback) dialog.callback(text);
                            }
                        }}
                        placeholder={dialog.data.placeholder} 
                        style={{padding: 5, borderRadius: 5, border: "1px solid black"}}
                        autoFocus
                    />
                    <button className={globalCSS.button} onClick={() => {
                        closeDialog();
                        if (dialog.callback) dialog.callback(text);
                    }}>{dialog.data.buttonText || t("dialog.Potvrdit")}</button>
                </div>
            );
            break;
        case "LUCKY_WHEEL":
            content = (
                <LuckyWheel options={dialog.data.options} chosedOptionId={dialog.data.chosedOptionId} onOk={()=>{
                    closeDialog();
                    if(dialog.callback) dialog.callback(dialog.data.chosedOptionId);
                }} />
            );
            break;
        default:
            break;
    }

    return createPortal((
        <>
        <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>
        <div className={css.dialogBackground} style={{cursor:(dialog.notClosable ? "not-allowed" : "auto"),display:(dialog.type == null ? "none" : "flex")}} onClick={()=>{if(dialog.notClosable){toast.error(t("dialog.Musíš si něco vybrat." ))}else{closeDialog()}}}>
            <div className={css.dialogBox} onClick={e => e.stopPropagation()} style={{cursor:"auto"}}>
                <div className={css.dialogHeader}>
                    {header}
                </div>
                <div className={css.dialogContent}>
                    {content}
                </div>
            </div>
        </div>
        </>
    ),document.body);
}