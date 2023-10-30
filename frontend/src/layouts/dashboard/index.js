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
import {useDispatch, useSelector} from "react-redux";
// @mui material components
import Grid from "@mui/material/Grid";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";

// Material Dashboard 2 React example components
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";
import ReportsBarChart from "examples/Charts/BarCharts/ReportsBarChart";
import ReportsLineChart from "examples/Charts/LineCharts/ReportsLineChart";
import ComplexStatisticsCard from "examples/Cards/StatisticsCards/ComplexStatisticsCard";

// Data
import reportsBarChartData from "layouts/dashboard/data/reportsBarChartData";
import reportsLineChartData from "layouts/dashboard/data/reportsLineChartData";

// Dashboard components
import Projects from "layouts/dashboard/components/Projects";
import OrdersOverview from "layouts/dashboard/components/OrdersOverview";

function Dashboard() {
  const { sales, tasks } = reportsLineChartData;
  const counterPerTopic = useSelector(state => state.deadLetters.counterPerTopic);
  const eventsPerDay = useSelector(state => state.deadLetters.eventsPerDay);

  const dataPerDay = {
    labels: ["M", "T", "W", "T", "F", "S", "S"],
    datasets: { label: "Failed events", data: eventsPerDay },
  }

  return (
    <DashboardLayout>
      <DashboardNavbar />
      <MDBox py={3}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="dark"
                icon="leaderboard"
                title="Failed events: "
                count={counterPerTopic ? counterPerTopic[Object.keys(counterPerTopic)[0]] : "N/A"}
                percentage={{
                  color: "success",
                  amount: "",
                  label: Object.keys(counterPerTopic).length > 0 ? Object.keys(counterPerTopic)[0] : "N/A",
                }}
              />
            </MDBox>
          </Grid>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                icon="leaderboard"
                title="Failed events: "
                count={counterPerTopic ? counterPerTopic[Object.keys(counterPerTopic)[1]] : "N/A"}
                percentage={{
                  color: "success",
                  amount: "",
                  label: Object.keys(counterPerTopic).length > 1 ? Object.keys(counterPerTopic)[1] : "N/A",
                }}
              />
            </MDBox>
          </Grid>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="success"
                icon="leaderboard"
                title="Failed events: "
                count={counterPerTopic ? counterPerTopic[Object.keys(counterPerTopic)[2]] : "N/A"}
                percentage={{
                  color: "success",
                  amount: "",
                  label: Object.keys(counterPerTopic).length > 2 ? Object.keys(counterPerTopic)[2] : "N/A"
                }}
              />
            </MDBox>
          </Grid>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="primary"
                icon="leaderboard"
                title="Failed events: "
                count={counterPerTopic ? counterPerTopic[Object.keys(counterPerTopic)[3]] : "N/A"}
                percentage={{
                  color: "success",
                  amount: "",
                  label: Object.keys(counterPerTopic).length > 3 ? Object.keys(counterPerTopic)[3] : "N/A",
                }}
              />
            </MDBox>
          </Grid>
        </Grid>
        <MDBox mt={4.5}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6} lg={4}>
              <MDBox mb={3}>
                <ReportsBarChart
                  color="info"
                  title="Failed Events per days"
                  description="Calculates all events on a program level"
                  date=""
                  chart={dataPerDay}
                />
              </MDBox>
            </Grid>
            <Grid item xs={12} md={6} lg={8}>
              <MDBox mb={3}>
                <ReportsLineChart
                  color="success"
                  title="Failed events per component"
                  description={
                    <>
                      Number of events per components.
                    </>
                  }
                  date=""
                  chart={dataPerDay}
                />
              </MDBox>
            </Grid>
            {/* <Grid item xs={12} md={6} lg={4}>
              <MDBox mb={3}>
                <ReportsLineChart
                  color="dark"
                  title="completed tasks"
                  description="Last Campaign Performance"
                  date="just updated"
                  chart={dataPerDay}
                />
              </MDBox>
            </Grid> */}
          </Grid>
        </MDBox>
        <MDBox>
          <Grid container spacing={3}>
            <Grid item xs={12} md={12} lg={12}>
              <Projects />
            </Grid>
            {/* <Grid item xs={12} md={6} lg={4}>
              <OrdersOverview />
            </Grid> */}
          </Grid>
        </MDBox>
      </MDBox>
      <Footer />
    </DashboardLayout>
  );
}

export default Dashboard;
