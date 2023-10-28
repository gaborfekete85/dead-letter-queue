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

// prop-types is a library for typechecking of props
import PropTypes from "prop-types";

// @mui material components
import Icon from "@mui/material/Icon";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDButton from "components/MDButton";

// Material Dashboard 2 React context
import { useMaterialUIController } from "context";
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import AltRouteIcon from '@mui/icons-material/AltRoute';
import Typography from "@mui/material/Typography";

function DeadLetterItem({ noGutter, eventType, service, topic, partition, partitionOffset, createdAt, dataAsJson, reason  }) {
  const [controller] = useMaterialUIController();
  const { darkMode } = controller;

  return (
    <MDBox
      component="li"
      display="flex"
      justifyContent="space-between"
      alignItems="flex-start"
      bgColor={darkMode ? "transparent" : "grey-100"}
      borderRadius="lg"
      p={3}
      mb={noGutter ? 0 : 1}
      mt={2}
    >
      <MDBox width="100%" display="flex" flexDirection="column">
        <MDBox
          display="flex"
          justifyContent="space-between"
          alignItems={{ xs: "flex-start", sm: "center" }}
          flexDirection={{ xs: "column", sm: "row" }}
          mb={2}
        >
          <MDTypography variant="button" fontWeight="medium" textTransform="capitalize">
            {eventType}
          </MDTypography>

          <MDBox display="flex" alignItems="center" mt={{ xs: 2, sm: 0 }} ml={{ xs: -1.5, sm: 0 }}>
            <MDBox mr={1}>
              <MDButton variant="text" color="error">
                <Icon>delete</Icon>&nbsp;Replay the event
              </MDButton>
            </MDBox>
            <MDButton variant="text" color={darkMode ? "success" : "success"}>
              <Icon>edit</Icon>&nbsp;Mark as resolved manually
            </MDButton>
          </MDBox>
        </MDBox>
        <MDBox mb={1} lineHeight={0}>
          <MDTypography variant="caption" color="text">
              Service:&nbsp;&nbsp;&nbsp;
              <MDTypography variant="caption" fontWeight="medium" textTransform="capitalize">
                {service}
              </MDTypography>
          </MDTypography>
          <MDBox mb={1} lineHeight={1}>
            <MDTypography variant="caption" color="text">
              Topic:&nbsp;&nbsp;&nbsp;
              <MDTypography variant="caption" fontWeight="medium" textTransform="capitalize">
                {topic}
              </MDTypography>
            </MDTypography>
          </MDBox>
          <MDTypography variant="caption" color="text">
            Partition:&nbsp;&nbsp;&nbsp;
            <MDTypography variant="caption" fontWeight="medium">
              {partition}
            </MDTypography>
          </MDTypography>

          <MDTypography variant="caption" color="text">
             - Offset:&nbsp;&nbsp;&nbsp;
            <MDTypography variant="caption" fontWeight="medium">
              {partitionOffset}
            </MDTypography>
          </MDTypography>
          <br/>
          <MDBox mb={1} lineHeight={2}>
            <MDTypography variant="caption" color="text">
              CreatedAt:&nbsp;&nbsp;&nbsp;
              <MDTypography variant="caption" fontWeight="medium" textTransform="capitalize">
                {createdAt}
              </MDTypography>
            </MDTypography>
          </MDBox>


          <MDBox mb={1} lineHeight={2}>
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
                        <pre style={{fontSize: "10px"}}>{dataAsJson === undefined ? "" : JSON.stringify(JSON.parse(dataAsJson), null, 2)}</pre>
                        <br/><hr/><br/>
                        <pre>Error</pre>
                        <pre style={{fontSize: "10px"}}>{reason}</pre>
                      </Typography>
                    </AccordionDetails>
                  </Accordion>
                  </MDBox>


        </MDBox>
      </MDBox>
    </MDBox>
  );
}

// Setting default values for the props of Bill
DeadLetterItem.defaultProps = {
  noGutter: false,
};

// Typechecking props for the Bill
// DeadLetterItem.propTypes = {
//   name: PropTypes.string.isRequired,
//   company: PropTypes.string.isRequired,
//   email: PropTypes.string.isRequired,
//   vat: PropTypes.string.isRequired,
//   noGutter: PropTypes.bool,
// };

export default DeadLetterItem;
