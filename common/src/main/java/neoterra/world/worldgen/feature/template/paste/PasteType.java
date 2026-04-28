package neoterra.world.worldgen.feature.template.paste;

import neoterra.world.worldgen.feature.template.template.FeatureTemplate;

public interface PasteType {
    Paste get(FeatureTemplate template);
}
