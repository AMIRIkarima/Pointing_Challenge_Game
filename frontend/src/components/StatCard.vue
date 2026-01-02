<template>
  <div class="card">
    <h3>{{ title }}</h3>
    <div class="value">{{ typeof value === 'number' ? value.toFixed(3) : value }}{{ unit }}</div>
    <p v-if="description" class="description">{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  value: [Number, String],
  unit: String,
  description: String
});
</script>

<style scoped>
.card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  text-align: center;
}
.value {
  font-size: 2rem;
  font-weight: bold;
  color: #2c3e50;
}
.description {
  font-size: 0.8rem;
  color: #64748b;
  margin-top: 5px;
}
</style>