<script lang="ts">
	import { format } from 'date-fns';
	import { fr } from 'date-fns/locale';

	export let day: Date;
</script>

<div class="day-card">
	<h2 class="day-card-header">
		{format(day, 'eeee dd MMMM yyyy', {locale: fr})}
	</h2>
	<div class="day-card-content">

	</div>
</div>

<style lang="scss">
	.day-card {
		border: 1px solid var(--color-theme-1);
		background: var(--color-bg-2);
		border-radius: var(--border-radius);
		&-header {
			padding: .5rem 1rem;
			margin: 0;
			border-bottom: 1px solid var(--color-theme-1);
		}
		&-content {
			padding: .5rem 1rem;
		}
	}
</style>
