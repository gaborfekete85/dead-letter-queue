import {combineReducers} from "redux";
import dltReducer from "./dltReducer";

const rootReducer = combineReducers({deadLetters: dltReducer});

export default rootReducer