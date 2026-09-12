<script lang="ts">
	let { children } = $props();
</script>

<svelte:head>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2.1.1/css/pico.min.css" />
</svelte:head>

{@render children?.()}
