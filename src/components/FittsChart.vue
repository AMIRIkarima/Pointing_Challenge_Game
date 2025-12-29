<script setup>
import { computed } from 'vue';
import { Scatter } from 'vue-chartjs';
import { Chart as ChartJS, LinearScale, PointElement, LineElement, Tooltip, Legend } from 'chart.js';

ChartJS.register(LinearScale, PointElement, LineElement, Tooltip, Legend);

const props = defineProps(['rawPoints', 'slope', 'intercept']);

const chartData = computed(() => {
  // 1. The actual gameplay data points (Scatter)
  const scatterData = props.rawPoints.map(p => ({ x: p.id, y: p.mt }));
  
  // 2. The regression line (Line) based on a + b * ID
  // We generate two points: min ID and max ID to draw the straight line
  const maxID = Math.max(...scatterData.map(d => d.x), 5); // Default to 5 if empty
  const lineData = [
    { x: 0, y: props.intercept }, 
    { x: maxID, y: props.intercept + (props.slope * maxID) }
  ];

  return {
    datasets: [
      {
        label: 'Gameplay Samples',
        data: scatterData,
        backgroundColor: '#42b983',
        type: 'scatter'
      },
      {
        label: 'Fitts Regression Line',
        data: lineData,
        borderColor: '#e74c3c',
        borderWidth: 2,
        showLine: true,
        pointRadius: 0,
        type: 'line' 
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  scales: {
    x: { title: { display: true, text: 'Index of Difficulty (ID)' } },
    y: { title: { display: true, text: 'Movement Time (s)' }, beginAtZero: true }
  }
};
</script>

<template>
  <div class="chart-container">
    <Scatter :data="chartData" :options="chartOptions" />
  </div>
</template>

<style scoped>
.chart-container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
</style>