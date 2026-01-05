<!-- https://matthewjamestaylor.com/2-column-layouts -->
<div class="day">
	<div class="meal-lunch">lunch</div>
	<div class="meal-dinner">dinner</div>
</div>

<style>
	.day {
		display: flex;
	}
	.day > * {
		width: 50%;
		padding: 1rem;
	}
	.meal-lunch {
		background-color: lightgrey;
	}
	.meal-dinner {
		background-color: darkgrey;
	}
</style>
