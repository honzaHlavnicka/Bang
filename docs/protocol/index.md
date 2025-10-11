# Dokumentace protokolu bangu
## 0. základní fungování protokolu
Zprávy se posílají websocketem jako String. jejich formát je buďto:

`<typ>:<payload>`

nebo

`<typ>`
## 1. Průběh a fáze
1) připojení pomocí websocketu
2) server pošle `welcome`
3) zahájení fáze před hrou
4) klient se připojí ke hře
5) fáze hra

## 2. rozpis fází
### 2.1 Fáze před hrou
#### 2.1.1 klient -> server
##### `serverInfo:` 

### 2.2 Fáze při hře
