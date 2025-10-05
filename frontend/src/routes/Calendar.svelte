<script lang="ts">
	import WeekCalendar from './WeekCalendar.svelte';
</script>

<div class="calendar">
	<header class="calendar-header">
		<h1>Votre semaine</h1>
	</header>
	<main class="calendar-content">
		<WeekCalendar />
	</main>
</div>

<style lang="scss">
	@use '../lib/styles/variables';

	.calendar {
		width: 100%;
		&-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin: 24px 0;
			h1 {
				grid-column: 2 / span 1;
				margin: 0;
			}
		}
	}

	@media (min-width: variables.$screen-m) {
		.calendar {
			&-header {
				display: grid;
				grid-template-columns: repeat(3, 1fr);
			}
		}
	}
</style>
