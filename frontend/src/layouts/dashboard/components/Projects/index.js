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

function Projects() {
  const { columns, rows } = data();
  const deadLetters = useSelector(state => state.deadLetters.list)
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
    let splittedStr = eventType.split(".");
    return splittedStr[splittedStr.length - 1];
  };
  // useEffect(() => {
  //   alert("DeadLetters changed 2");
  //   alert(JSON.stringify(deadLetters));
  // }, [deadLetters]);

  useEffect(() => {
    if(deadLetters !== undefined) {
      // alert("DeadLetters changed 1");
      // alert(JSON.stringify(deadLetters));
      setLoaded("loaded");
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
      <MDBox display="flex" justifyContent="space-between" alignItems="center" p={3}>
        <MDBox>
          <MDTypography variant="h6" gutterBottom>
            Projects
          </MDTypography>
          <MDBox display="flex" alignItems="center" lineHeight={0}>
            <Icon
              sx={{
                fontWeight: "bold",
                color: ({ palette: { info } }) => info.main,
                mt: -0.5,
              }}
            >
              done
            </Icon>
            <MDTypography variant="button" fontWeight="regular" color="text">
              &nbsp;<strong>30 done</strong> this month
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
              <TableRow key={index}>
                <TableCell>{shortenEventType(item.eventType)}</TableCell>
                <TableCell>{item.topic + "(P:  " + item.partition + ", O:" + item.partitionOffset + ")"}</TableCell>
                <TableCell>{item.createdAt}</TableCell>
                {/* <TableCell>{shortenEventType(item.eventType)}</TableCell> */}
              </TableRow>
              <TableRow>
                  <TableCell colSpan={3}><pre>{item.reason.substring(0, 100)} ... </pre></TableCell>
              </TableRow>
              <TableRow>
                  <TableCell colSpan={3}><pre>{JSON.stringify(JSON.parse(item.dataAsJson), null, 2).substring(0, 200)} ... </pre></TableCell>
              </TableRow>
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
