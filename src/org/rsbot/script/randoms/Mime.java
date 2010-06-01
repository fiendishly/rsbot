package org.rsbot.script.randoms;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "PwnZ", "Taha", "Zenzie" }, name = "Mime", version = 1.3)
public class Mime extends Random {

	private enum Stage {
		click, findMime, findAnimation, clickAnimation, wait
	}

	private int animation;
	private RSNPC mime;

	@Override
	public boolean activateCondition() {
		final RSNPC mime = getNearestNPCByID(1056);
		return (mime != null) && (distanceTo(mime.getLocation()) < 15);
	}

	private boolean clickAnimation(final String find) {
		if (!RSInterface.getInterface(188).isValid())
			return false;
		for (int a = 0; a < RSInterface.getInterface(188).getChildCount(); a++) {
			if (RSInterface.getInterface(188).getChild(a).getText().contains(find)) {
				log("Clicked on: " + find);
				return atInterface(RSInterface.getInterface(188).getChild(a));
			}
		}
		return false;
	}

	private RSNPC getNPCAt(final RSTile t) {
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
		for (final int element : validNPCs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				if (Monster.getLocation().equals(t))
					return Monster;
			} catch (final Exception ignored) {
			}
		}
		return null;
	}

	private Stage getStage() {
		if (canContinue() && getMyPlayer().getLocation().equals(new RSTile(2008, 4764)))
			return Stage.click;
		else if (mime == null)
			return Stage.findMime;
		else if ((RSInterface.getInterface(372).getChild(2).getText().contains("Watch") || RSInterface.getInterface(372).getChild(3).getText().contains("Watch")) && (mime.getAnimation() != -1) && (mime.getAnimation() != 858))
			return Stage.findAnimation;
		else if (RSInterface.getInterface(188).isValid())
			return Stage.clickAnimation;
		else
			return Stage.wait;
	}

	@Override
	public int loop() {
		if (!activateCondition())
			return -1;
		switch (getStage()) {
			case click:
				clickContinue();
				wait(random(1500, 2000));
				return random(200, 400);

			case findMime:
				if (((mime = getNearestNPCByID(1056)) == null) && ((mime = getNPCAt(new RSTile(2011, 4762))) == null)) {
					log.warning("ERROR: Mime not found!");
					return -1;
				}
				return random(200, 400);

			case findAnimation:
				animation = mime.getAnimation();
				log.info("Found Mime animation: " + animation);
				wait(1000);
				if (RSInterface.getInterface(188).isValid())
					return random(400, 800);
				final long start = System.currentTimeMillis();
				while (System.currentTimeMillis() - start >= 5000) {
					if (RSInterface.getInterface(188).isValid())
						return random(1000, 1600);
					wait(random(1000, 1500));
				}
				return random(200, 400);

			case clickAnimation:
				log.info("Clicking text according to animation: " + animation);
				if ((animation != -1) && (animation != 858)) {
					switch (animation) {
						case 857:
							clickAnimation("Think");
							break;
						case 860:
							clickAnimation("Cry");
							break;
						case 861:
							clickAnimation("Laugh");
							break;
						case 866:
							clickAnimation("Dance");
							break;
						case 1128:
							clickAnimation("Glass Wall");
							break;
						case 1129:
							clickAnimation("Lean");
							break;
						case 1130:
							clickAnimation("Rope");
							break;
						case 1131:
							clickAnimation("Glass Box");
							break;
						default:
							log.info("Unknown Animation: " + animation + " Please inform a developer at RSBot.org!");
							return random(2000, 3000);
					}
				}
			case wait:
				return random(200, 400);
		}
		return random(200, 400);
	}
}