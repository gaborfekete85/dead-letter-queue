import {DLT_QUERIED, NEW_DLT} from "./actionTypes";

export const queryDlt = (deadLetters) => {
    return {
        type: DLT_QUERIED,
        payload: deadLetters,
    };
};

export const addNewDltEvent = (event) => {
    return {
        type: NEW_DLT,
        payload: event,
    };
};

// export const updateTaskTitle = (value) => {
//     return {
//         type: UPDATE_TASK_TITLE,
//         payload: value,
//     };
// };

// export const updateTaskDescription = (value) => {
//     return {
//         type: UPDATE_TASK_DESCRIPTION,
//         payload: value,
//     };

