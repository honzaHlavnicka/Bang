import { createPortal } from "react-dom";
import css from "../styles/dialog.module.css";
import { useDialog } from "../modules/DialogContext";
import Card from "./Card";
import type { CardType } from "../modules/GameContext";
import ZoomToggleButton from "./ZoomButton";
import { useState } from "react";
import toast from "react-hot-toast";
import DarkModeSwitch from "./DarkModeSwitch";
import globalCSS from "../styles/global.module.css";


export default function Dialog() {
    const {closeDialog,dialog} = useDialog();
    const [seleckted,setSelected] = useState<Array<number>>([]);
    let maxSelected = 0;
    if(dialog == null){
        return;
    }

    const header = dialog.dialogHeader || dialog.type || "Dialog";


    const select = (id:number) => {
        if(seleckted.includes(id)){
            setSelected(seleckted.filter(v=>v!=id));
        } else {
            if(seleckted.length >= maxSelected){
                setSelected([...seleckted.slice(1),id]);
            }else{
                setSelected([...seleckted,id]);
            }
        }
    };


    let content: React.ReactNode;
    switch (dialog.type) {
        case "SELECT_CARD": 
            maxSelected = dialog.data.max;
            content = (
                <div>
                    <div style={{display:"flex",flexDirection:"row",flexWrap:"wrap"}} >
                        {dialog.data.cards.map((val:CardType)=>(
                            <div key={val.id} style={{background:(seleckted.includes(val.id) ? "brown" : "transparent"),borderRadius:10,margin:5,padding:2}} onClick={()=>select(val.id)} >
                                <Card key={val.id} id={val.id}  image={val.image}/>
                            </div>
                        ))}
                    </div>
                    <div>Vybráno {seleckted.length} z {dialog.data.max}. (minimálně je potřeba {dialog.data.min}.)</div>
                    <ZoomToggleButton/>
                </div>
            );
            break;
        case "SELECT_PLAYER":
                maxSelected = 1;
                content = (
                    
                    <div style={{display:"flex",flexDirection:"column",alignItems:"flex-start"}} >

                        {dialog.data.players.map((val:{id:number,name:string})=>(
                            <>
                            
                                <label key={val.id} style={{background:(seleckted.includes(val.id) ? "grey" : "transparent"),borderRadius:10,margin:3,padding:2,cursor:"pointer"}} >
                                    <input type="radio" name="player" value={val.id} style={{marginRight:10}} onChange={()=>select(val.id)} checked={seleckted.includes(val.id)}/>
                                    {val.name}
                                </label>
                            
                            <br/>
                            </>
                        ))}
                        {seleckted.length > 0 ? <button onClick={()=>{
                            closeDialog();
                            dialog.callback(seleckted[0]);
                            setSelected([]);
                        }} className={globalCSS.button} >Potvrdit</button> : <div>Vyberte hráče.</div>}
                    </div>
                );
            break;
        case "CONFIRM_ACTION":
            maxSelected = 1;
            content = (
                <div style={{display:"flex",flexDirection:"row",alignItems:"flex-start",flexWrap:"wrap",gap:10}} >
                    {dialog.data.actions.map((val:{id:number,name:string})=>(   
                            <button className={globalCSS.button}  key={val.id} onClick={()=>{
                                closeDialog();
                                if(dialog.callback){
                                    dialog.callback(val.id);
                                }
                            }} >
                                {val.name}
                            </button>
                    ))}
                </div>
            );
            break;
        case "INFO":
            content = (
                <div style={{display:"flex",flexDirection:"column",alignItems:"center",gap:5}} >
                    {dialog.data.image ? <img src={dialog.data.image} alt={dialog.data.header} style={{width:"100%"}}/>: null}
                    {dialog.data.header ? <h2>{dialog.data.header}</h2> : null}
                    <p>{dialog.data.message}</p>
                    <button className={globalCSS.button} onClick={()=>{
                        closeDialog();
                    }}>OK</button>
                </div>
            );
        break;
        default:
            break;
    }
    


    return createPortal((
        <>
        <DarkModeSwitch style={{position:"fixed",top:10,left:10,zIndex:1005,fontSize:"2em"}}/>
        <div className={css.dialogBackground} style={{cursor:(dialog.notCloasable ? "not-allowed" : "auto"),display:(dialog.type == null ? "none" : "flex")}} onClick={()=>{if(dialog.notCloasable){toast.error("Musíš si něco vybrat.")}else{closeDialog()}}}>
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