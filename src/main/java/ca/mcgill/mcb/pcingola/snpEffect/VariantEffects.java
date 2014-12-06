package ca.mcgill.mcb.pcingola.snpEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.interval.Transcript;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.snpEffect.VariantEffect.EffectImpact;
import ca.mcgill.mcb.pcingola.snpEffect.VariantEffect.ErrorWarningType;
import ca.mcgill.mcb.pcingola.util.Gpr;

/**
 * A sorted collection of variant effects
 *
 * @author pcingola
 */
public class VariantEffects implements Iterable<VariantEffect> {

	List<VariantEffect> effects;

	public VariantEffects(Variant variant) {
		effects = new ArrayList<VariantEffect>();
	}

	/**
	 * Add an effect
	 */
	public void add(Variant variant, Marker marker, EffectType effectType, EffectImpact effectImpact, String message) {
		VariantEffect effNew = new VariantEffect(variant);
		effNew.set(marker, effectType, effectImpact, message);
		add(effNew);
	}

	/**
	 * Add an effect
	 */
	public void add(Variant variant, Marker marker, EffectType effectType, String message) {
		add(variant, marker, effectType, effectType.effectImpact(), message);
	}

	/**
	 * Add an effect
	 */
	public void add(VariantEffect variantEffect) {
		effects.add(variantEffect);
	}

	/**
	 * Add: If possible, only add an effect type (otherwise add the full effect)
	 */
	public void addEffectType(Variant variant, Marker marker, EffectType effectType) {
		if (canAddType(variant, marker)) {
			get().addEffect(effectType);
		} else add(variant, marker, effectType, effectType.effectImpact(), "");
	}

	public void addErrorWarning(ErrorWarningType errwarn) {
		VariantEffect veff = get();
		if (veff != null) veff.addErrorWarningInfo(errwarn);
		else Gpr.debug("Could not get latest " + VariantEffect.class.getSimpleName());
	}

	/**
	 * Can we add an effectType to the previous variatnEffect?
	 * @return true if transcript IDs and variant's genotypes match (i.e. we can add effectType)
	 */
	boolean canAddType(Variant variant, Marker marker) {
		VariantEffect veff = get();
		if (veff == null || veff.getVariant() == null) return false;

		// Do genotypes match?
		String gt = veff.getVariant().getGenotype();
		String vgt = variant.getGenotype();
		if (((vgt != null) ^ (gt != null)) // One null and one non-null?
				|| ((vgt != null) && (gt != null) && !variant.getGenotype().equals(variant.getGenotype())) // Both non-null, but different?
		) return false;

		// Do transcripts match?
		Transcript trMarker = (Transcript) marker.findParent(Transcript.class);
		Transcript tr = veff.getTranscript();
		if (tr == null || trMarker == null) return false;

		return tr.getId().equals(trMarker.getId());
	}

	/**
	 * Get (or create) the latest ChangeEffect
	 */
	public VariantEffect get() {
		if (effects.isEmpty()) return null;
		return effects.get(effects.size() - 1);
	}

	public VariantEffect get(int index) {
		return effects.get(index);
	}

	public boolean isEmpty() {
		return effects.isEmpty();
	}

	@Override
	public Iterator<VariantEffect> iterator() {
		return effects.iterator();
	}

	public void setMarker(Marker marker) {
		VariantEffect veff = get();
		if (veff != null) veff.setMarker(marker);
		else Gpr.debug("Could not get latest VariantEffect");
	}

	public int size() {
		return effects.size();
	}

	public void sort() {
		Collections.sort(effects);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (VariantEffect eff : this)
			sb.append(eff + "\n");
		return sb.toString();
	}
}
