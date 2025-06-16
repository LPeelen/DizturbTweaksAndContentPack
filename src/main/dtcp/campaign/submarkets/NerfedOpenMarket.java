package dtcp.campaign.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.OpenMarketPlugin;
import dtcp.util.Setting;
import org.apache.log4j.Logger;

import java.util.List;

public class NerfedOpenMarket extends OpenMarketPlugin {

    public static final Setting<List<String>> LEGAL_SHIPS = new Setting<>("legalShips", List.of(), String.class);
    private static final Logger LOGGER = Global.getLogger(NerfedOpenMarket.class);

    private String marketName;
    private String subMarketName;

    @Override
    public void init(SubmarketAPI submarket) {
        super.init(submarket);
        marketName = submarket.getMarket().getName();
        subMarketName = submarket.getName();
    }

    @Override
    public void updateCargoPrePlayerInteraction() {
        super.updateCargoPrePlayerInteraction();

        // Just remove Mudskippers from the catalog
        FleetDataAPI shipsForSale = submarket.getCargo().getMothballedShips();
        for (FleetMemberAPI ship : shipsForSale.getMembersListCopy()) {
            String hullName = ship.getHullSpec().getHullId();
 
            if (LEGAL_SHIPS.get().contains(hullName)) {
                continue;
            }

            shipsForSale.removeFleetMember(ship);

            LOGGER.info(new StringBuilder("Removing ship: ")
                    .append(hullName)
                    .append(" from: ")
                    .append(marketName)
                    .append("/")
                    .append(subMarketName));
        }
    }
}
