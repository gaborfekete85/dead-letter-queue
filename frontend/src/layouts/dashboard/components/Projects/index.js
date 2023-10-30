/**
=========================================================
* Material Dashboard 2 React - v2.2.0
=========================================================

* Product Page: https://www.creative-tim.com/product/material-dashboard-react
* Copyright 2023 Creative Tim (https://www.creative-tim.com)

Coded by www.creative-tim.com

 =========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*/
import * as React from 'react';
import { useState, useEffect } from "react";

// @mui material components
import Card from "@mui/material/Card";
import Icon from "@mui/material/Icon";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";

// Material Dashboard 2 React examples
import DataTable from "examples/Tables/DataTable";

// Data
import data from "layouts/dashboard/components/Projects/data";
import axios from "axios";

import Box from "@mui/material/Box";
import Collapse from "@mui/material/Collapse";
import IconButton from "@mui/material/IconButton";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Typography from "@mui/material/Typography";
import Paper from "@mui/material/Paper";
import {useDispatch, useSelector} from "react-redux";

import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import AltRouteIcon from '@mui/icons-material/AltRoute';

// Billing page components
import DeadLetterItem from "layouts/billing/components/DeadLetterItem";
import Modal from '@mui/material/Modal';
import Button from '@mui/material/Button';

// import Table from '@mui/material/Table';
// import TableHead from '@mui/material/TableHead';
// import TableRow from '@mui/material/TableRow';
// import TableCell from '@mui/material/TableCell';
// import TableBody from '@mui/material/TableBody';

// import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
// import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
// import Accordion from "@mui/material/Accordion";
// import AccordionDetails from "@mui/material/AccordionDetails";
// import AccordionSummary from "@mui/material/AccordionSummary";
// import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

import {queryDlt, addNewDltEvent} from "store/actions";
import TextField from '@mui/material/TextField';
import producerService from "services/ProducerService";

function Projects() {
  const { columns, rows } = data();
  const dispatch = useDispatch();
  const deadLetters = useSelector(state => state.deadLetters.list);
  const countOfdeadLetters = useSelector(state => state.deadLetters.counter);
  const counterPerTopic = useSelector(state => state.deadLetters.counterPerTopic);
  
  const [open, setOpen] = React.useState(false);
  const [resendTopic, setResendTopic] = React.useState("");
  const [resendDltKey, setResendDltKey] = React.useState("");

  const handleOpen = (dltKey, originalTopic) => {
    setResendTopic(originalTopic + "_retry");
    setResendDltKey(dltKey);
    // alert("DltKey: " + dltKey + ", Original Topic: " + originalTopic)
    setOpen(true);
  };
  const handleClose = () => setOpen(false);

  const handleResend = () => {
    producerService.resendEvent({ 
      "dltTopicEventKey": resendDltKey,
      "topic": resendTopic 
    }).then((res) => { 
      // alert(res.data );
      handleClose();
    });
  };

  const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 700,
    bgcolor: '#ecf4f8',
    borderRadius: 3,
    // border: '1px solid #000',
    boxShadow: 24,
    p: 4,
  };


  // const [expanded, setExpanded] = React.useState(false);

  // const handleChange = (panel) => (event, isExpanded) => {
  //   setExpanded(isExpanded ? panel : false);
  // };

  // useEffect(() => {
  //   alert("Start HTTP GET request ... ");
  //   axios.get("http://localhost:8300/dlt/api/dlt").then((res) => {
  //     // alert("Response: " + JSON.stringify(res.data));
  //     res.data.forEach((dlt) => {
  //       let dltObj = {
  //         companies: <Company image={logoXD} name={dlt.dltKey} />,
  //         members: (
  //           <MDBox display="flex" py={1}>
  //             {avatars([
  //               [team1, "Ryan Tompson"],
  //               [team2, "Romina Hadid"],
  //               [team3, "Alexander Smith"],
  //               [team4, "Jessica Doe"],
  //             ])}
  //           </MDBox>
  //         ),
  //         budget: (
  //           <MDTypography variant="caption" color="text" fontWeight="medium">
  //             $14,000
  //           </MDTypography>
  //         ),
  //         completion: (
  //           <MDBox width="8rem" textAlign="left">
  //             <MDProgress value={60} color="info" variant="gradient" label={false} />
  //           </MDBox>
  //         ),
  //       };
  //       alert(JSON.stringify(dltObj))
  //     })
  //   });
  // }, []);
  const [menu, setMenu] = useState(null);

  const openMenu = ({ currentTarget }) => setMenu(currentTarget);
  const closeMenu = () => setMenu(null);
  
  const [loaded, setLoaded] = useState("loading");

  const shortenEventType = (eventType) => {
    if(!eventType) {
      return "";
    }

    let splittedStr = eventType.split(".");
    return splittedStr[splittedStr.length - 1];
  };

  const updateEvents = (event) => {
    console.log("event: " + event);
    dispatch(addNewDltEvent(event));
  }

  useEffect(() => {
    //fetchStockPrice();
    // alert("Start streaming ... ");
    const eventSource = new EventSource(`/dlt/api/dlt/stream`);
    eventSource.onmessage = (e) => updateEvents(e.data);
    eventSource.onerror = (error) => {
      console.error('Error:', error);
    };
    return () => {
      eventSource.close();
    };
  }, []);

  // useEffect(() => {
  //   axios.get("/dlt/api/dlt").then((res) => {
  //     dispatch(queryDlt(res.data))
  //   });
  //   const eventSource = new EventSource("http://localhost:8300/dlt/api/dlt/stream");
  //   eventSource.onmessage = (e) => {
  //     console.log("Event received");
  //     // alert(JSON.stringify(e));
  //     // addNewEvent(e.data);
  //   };
  // }, []);

  const addNewEvent = (data) => {
    const parsedData = JSON.parse(data);
    console.log("Event failed " + JSON.stringify(data));
    // dispatch({
    //   type: POSITION_UPDTATED,
    //   payload: parsedData,
    // });
  };

  useEffect(() => {
    if(deadLetters !== undefined) {
      // alert("DeadLetters changed 1");
      // alert(JSON.stringify(deadLetters));
      setLoaded("loaded");
      // return () => {
      //   eventSource.close();
      // };
    }
  }, [deadLetters]);

  const renderMenu = (
    <Menu
      id="simple-menu"
      anchorEl={menu}
      anchorOrigin={{
        vertical: "top",
        horizontal: "left",
      }}
      transformOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      open={Boolean(menu)}
      onClose={closeMenu}
    >
      <MenuItem onClick={closeMenu}>Action</MenuItem>
      <MenuItem onClick={closeMenu}>Another action</MenuItem>
      <MenuItem onClick={closeMenu}>Something else</MenuItem>
    </Menu>
  );

  return (
    <Card>
          <Card id="delete-account">
      <MDBox pt={3} px={2}>
        <MDTypography variant="h6" fontWeight="medium">
          Failed Events
        </MDTypography>
        <MDBox display="flex" alignItems="center" lineHeight={0}>
            <AltRouteIcon color="primary"/>
            {/* <Icon
              sx={{
                fontWeight: "bold",
                color: ({ palette: { info } }) => info.main,
                mt: -0.5,
              }}
            >
              altRoute
            </Icon> */}
            <MDTypography variant="button" fontWeight="regular" color="text">
              &nbsp;<strong>{countOfdeadLetters}</strong> events failed. 
            </MDTypography>
          </MDBox>

      </MDBox>
      <MDBox pt={1} pb={2} px={2}>

      <MDBox
          display="flex"
          justifyContent="space-between"
          alignItems={{ xs: "flex-start", sm: "center" }}
          flexDirection={{ xs: "column", sm: "row" }}
          mb={2}
        >
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <Typography id="modal-modal-title" variant="h6" component="h2">
            Resend the event to: 
          </Typography>
          <Typography id="modal-modal-description" sx={{ mt: 2 }}>
              {resendDltKey}
            </Typography>
          <Typography id="modal-modal-description" sx={{ mt: 2 }}>
            <TextField style={{ width: "100%", fontColor: "#fff" }} id="outlined-basic" label="Topic" variant="outlined" value={resendTopic} 
            onChange={(event) => {
              setResendTopic(event.target.value);
              }}
            />
            <Button style={{ width: "100%" }} color="success" variant="contained" onClick={handleResend}>Submit >> </Button>
          </Typography>
        </Box>
      </Modal>
      </MDBox>

      <MDBox component="ul" display="flex" flexDirection="column" p={0} m={0}>
        
      {deadLetters.map((item, index) => (
          <DeadLetterItem
            asString={JSON.stringify(item)}
            dltKey={item.dltKey}
            eventType={shortenEventType(item.eventType)}
            service={item.serviceId}
            topic={item.topic}
            partition={item.partition}
            partitionOffset={item.partitionOffset}
            createdAt={item.createdAt}
            dataAsJson={item.dataAsJson}
            reason={item.reason}
            resendCallback={handleOpen}
            noGutter
          />    
      ))}
        </MDBox>
      </MDBox>
    </Card>
    </Card>
  );
}

export default Projects;
