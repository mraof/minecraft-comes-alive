package mca.core.radix;

import mca.core.MCA;
import mca.data.PlayerData;
import mca.data.PlayerMemory;
import mca.entity.EntityHuman;
import net.minecraft.entity.player.EntityPlayer;
import radixcore.lang.AbstractLanguageParser;
import radixcore.util.RadixExcept;

public class LanguageParser extends AbstractLanguageParser
{
	@SuppressWarnings("null")
	@Override
	public String parsePhrase(String unparsedPhrase, Object[] arguments) 
	{
		int passes = 0;
		EntityHuman entitySpeaker = (EntityHuman) this.getArgumentOfType(arguments, EntityHuman.class, 1);
		EntityHuman entitySecondary = (EntityHuman) this.getArgumentOfType(arguments, EntityHuman.class, 2);
		EntityPlayer playerTarget = (EntityPlayer) this.getArgumentOfType(arguments, EntityPlayer.class);
		PlayerMemory memory = (entitySpeaker != null && playerTarget != null) ? entitySpeaker.getPlayerMemory(playerTarget) : null;
		
		//Allow at most 10 passes to avoid infinite loops.
		while (unparsedPhrase.contains("%"))
		{
			try
			{
				if (unparsedPhrase.contains("%Name%"))
				{
					unparsedPhrase = unparsedPhrase.replace("%Name%", entitySpeaker.getName());
				}
				
				else if (unparsedPhrase.contains("%Profession%"))
				{
					unparsedPhrase = unparsedPhrase.replace("%Profession%", entitySpeaker.getProfessionEnum().getUserFriendlyForm());
				}
				
				else if (unparsedPhrase.contains("%FatherName%"))
				{
					String parentName = entitySpeaker.getParentNames();
					unparsedPhrase = unparsedPhrase.replace("%FatherName%", parentName.subSequence(0, parentName.indexOf("|")));
				}
				
				else if (unparsedPhrase.contains("%MotherName%"))
				{
					String parentNames = entitySpeaker.getParentNames();
					unparsedPhrase = unparsedPhrase.replace("%MotherName%", parentNames.subSequence(parentNames.indexOf("|") + 1, parentNames.length()));
				}
				
				else if (unparsedPhrase.contains("%PlayerName%"))
				{
					PlayerData data = MCA.getPlayerData(playerTarget);
					unparsedPhrase = unparsedPhrase.replace("%PlayerName%", data.mcaName.getString());
				}
				
				else if (unparsedPhrase.contains("%ParentOpposite%"))
				{
					boolean isPlayerMale = MCA.getPlayerData(playerTarget).isMale.getBoolean();
					
					if (isPlayerMale)
					{
						unparsedPhrase = unparsedPhrase.replace("%ParentOpposite%", MCA.getLanguageManager().getString("parser.mom"));
					}
					
					else
					{
						unparsedPhrase = unparsedPhrase.replace("%ParentOpposite%", MCA.getLanguageManager().getString("parser.dad"));						
					}
				}
				
				else if (unparsedPhrase.contains("%ParentTitle%"))
				{
					boolean isPlayerMale = MCA.getPlayerData(playerTarget).isMale.getBoolean();
					
					if (!isPlayerMale)
					{
						unparsedPhrase = unparsedPhrase.replace("%ParentTitle%", MCA.getLanguageManager().getString("parser.mom"));
					}
					
					else
					{
						unparsedPhrase = unparsedPhrase.replace("%ParentTitle%", MCA.getLanguageManager().getString("parser.dad"));						
					}					
				}
				
				else if (unparsedPhrase.contains("%a1%"))
				{
					unparsedPhrase = unparsedPhrase.replace("%a1%", arguments[0].toString());
				}
				
				else if (unparsedPhrase.contains("%a2%"))
				{
					unparsedPhrase = unparsedPhrase.replace("%a2%", arguments[1].toString());					
				}
			}

			catch (Exception e)
			{
				RadixExcept.logErrorCatch(e, "Exception while parsing phrase.");
			}

			finally
			{
				passes++;
				
				if (passes >= 10)
				{
					Throwable t = new Throwable();
					RadixExcept.logErrorCatch(t, "Too many passes through parser! Some text isn't displaying correctly.");
					break;
				}
			}
		}

		return unparsedPhrase;
	}
}
