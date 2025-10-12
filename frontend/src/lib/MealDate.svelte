<script lang="ts">
	interface Props {
		date?: Date;
	}
	let { date }: Props = $props();
</script>

{#if date}
	{date.toLocaleDateString('fr-FR')}
	{#if date.getHours() === 12}midi{:else}soir{/if}
{/if}
