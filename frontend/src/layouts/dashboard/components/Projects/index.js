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
import Bill from "layouts/billing/components/Bill";
import DeadLetterItem from "layouts/billing/components/DeadLetterItem";

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

function Projects() {
  const { columns, rows } = data();
  const dispatch = useDispatch();
  const deadLetters = useSelector(state => state.deadLetters.list);
  const countOfdeadLetters = useSelector(state => state.deadLetters.counter);
  const counterPerTopic = useSelector(state => state.deadLetters.counterPerTopic);
  
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
      <MDBox component="ul" display="flex" flexDirection="column" p={0} m={0}>
      {deadLetters.map((item, index) => (
          <DeadLetterItem
            name={shortenEventType(item.eventType)  }
            company="viking burrito"
            email="oliver@burrito.com"
            vat="FRB1235476"
            eventType={shortenEventType(item.eventType)}
            service="PCO"
            topic={item.topic}
            partition={item.partition}
            partitionOffset={item.partitionOffset}
            createdAt={item.createdAt}
            dataAsJson={item.dataAsJson}
            reason={item.reason}
            noGutter
          />    
      ))}
        
          <Bill
            name="oliver liam"
            company="viking burrito"
            email="oliver@burrito.com"
            vat="FRB1235476"
          />
          <Bill
            name="lucas harper"
            company="stone tech zone"
            email="lucas@stone-tech.com"
            vat="FRB1235476"
          />
          <Bill
            name="ethan james"
            company="fiber notion"
            email="ethan@fiber.com"
            vat="FRB1235476"
            noGutter
          />
        </MDBox>
      </MDBox>
    </Card>
    
      <MDBox display="flex" justifyContent="space-between" alignItems="center" p={3}>
        <MDBox>
          <MDTypography variant="h6" gutterBottom>
            {/* <pre>
              {JSON.stringify(counterPerTopic, null, 4)}
            </pre> */}
            {/* <h2>Top Failure topic</h2>
            <pre>
              {counterPerTopic ? Object.keys(counterPerTopic)[0] + " (" + counterPerTopic[Object.keys(counterPerTopic)[0]] + ")" : "No failed event yet"}
            </pre> */}
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
        <MDBox color="text" px={2}>
          <Icon sx={{ cursor: "pointer", fontWeight: "bold" }} fontSize="small" onClick={openMenu}>
            more_vert
          </Icon>
        </MDBox>
        {renderMenu}
      </MDBox>
      <TableContainer style={{ width: '100%' }}>
        {loaded === "loading" ? (
          <p>Loading ... </p>
        ) : (
          <Table style={{ width: '100%' }}>
          {/* <TableHead style={{ width: '100%' }}>
            <TableRow style={{ width: '100%' }}>
              <TableCell>Column 1</TableCell>
              <TableCell>Column 2</TableCell>
              <TableCell>Column 3</TableCell>
            </TableRow>
          </TableHead> */}
          <TableBody>
            <TableRow>
              <TableCell><strong>Event Type</strong></TableCell>
              <TableCell><strong>Topic</strong></TableCell>
              <TableCell><strong>Created At</strong></TableCell>
            </TableRow>
            {deadLetters.map((item, index) => (
              <>
              <TableRow style={{ borderTop: "2px solid #33b864" }} key={index + "_1"}>
                <TableCell>{shortenEventType(item.eventType)}</TableCell>
                <TableCell>{item.topic + " - Partition:  " + item.partition + ", Offset:" + item.partitionOffset}</TableCell>
                <TableCell>{item.createdAt}</TableCell>
                {/* .substring(0, 200) */}
              </TableRow>
              <TableRow key={index + "_2"}>
                  <TableCell colSpan={3}>
                  <Accordion>
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel1a-content"
                      id="panel1a-header"
                    >
                      <Typography>Details</Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                      <Typography>
                        <pre>Event</pre>
                        <pre style={{fontSize: "10px"}}>{item.dataAsJson === undefined ? "" : JSON.stringify(JSON.parse(item.dataAsJson), null, 2)}</pre>      
                        <br/><hr/><br/>
                        <pre>Error</pre>
                        <pre style={{fontSize: "10px"}}>{item.reason}</pre>
                      </Typography>
                    </AccordionDetails>
                  </Accordion>
                    
                  </TableCell>
              </TableRow>
              {/* <TableRow key={index + "_3"}>
                  <TableCell colSpan={3}><pre>{item.dataAsJson === undefined ? "" : JSON.stringify(JSON.parse(item.dataAsJson), null, 2).substring(0, 200)} ... </pre></TableCell>
              </TableRow> */}
              </>
            ))}
          </TableBody>
        </Table>
        )}
      </TableContainer>
    </Card>
  );
}

export default Projects;
