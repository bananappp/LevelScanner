package org.geotools.steamtest;
import com.github.koraktor.steamcondenser.steam.community.*;
import com.github.koraktor.steamcondenser.steam.community.tf2.*;
import java.util.*;
import java.util.regex.*;
/**
 * Hello world!
 *
 */
public class App 
{
    private static Pattern regex = Pattern.compile("STEAM_\\d:\\d:\\d{6,8}");
    private static Matcher find;
    private static int counter = 0;
    
    private static Map<String, String> rarity = new HashMap();
    static {
        rarity.put("rarity1", "Genuine");
        rarity.put("strange", "Strange");
        rarity.put("rarity4", "Unusual");
        rarity.put("vintage", "Vintage");
        rarity.put("haunted", "Haunted");
        rarity.put("festive", "Festive");
    }
    
    public App(){
        try{
            WebApi.setApiKey("114B2E0607289194DC61919B4E19682E");
        }
        catch(Exception e) {
            System.out.println("Couldn't authenticate.");
        }
    }
    
    public static SteamId[] getIds(String status) throws Exception {
        find = regex.matcher(status);
        ArrayList<SteamId> ids = new ArrayList();
        while(find.find()) {
            try{
                SteamId curr = makeId(SteamId.convertSteamIdToCommunityId(find.group()));
                if(curr != null && hasTF2(curr)) {
                    ids.add(curr);
                }
            }
            catch(Exception e) {
                System.out.println("Error getting an id: "+e.getMessage());
                return new SteamId[0];
            }
        }
        SteamId[] i = new SteamId[ids.size()];
        return ids.toArray(i);
    }
    
    public String[] getLeveledItems(int level, SteamId player, boolean showWeapons) {
        LinkedList<String> leveledItems = new LinkedList();
        TF2Item[] bp = getBackpack(player);
        for(TF2Item item:bp) {
            if(item.getLevel() == level) {
                try{
                    System.out.println(item.getClassName());
                    if(!item.getClassName().equals("tf_wearable")){
                        if(showWeapons) {
                            System.out.println("Adding weapon.");
                            leveledItems.add(("Level "+item.getLevel()+" ")+(rarity.containsKey(item.getQuality())?rarity.get(item.getQuality()):item.getQuality()) + " " + item.getName());
                        }
                    }
                    else{
                        System.out.println("Adding non-weapon.");
                        leveledItems.add(("Level "+item.getLevel()+" ")+(rarity.containsKey(item.getQuality())?rarity.get(item.getQuality()):item.getQuality()) + " " + item.getName());
                    }
                }
                catch(Exception e) {
                    System.out.println("");
                }
            }
        }
        return leveledItems.toArray(new String[leveledItems.size()]);
    }
    
    private TF2Item[] getBackpack(SteamId user) {
        TF2Inventory s;
        TF2Item[] inv;
        try{
            s = TF2Inventory.create(user.getSteamId64());
        }
        catch(Exception e) {
            System.out.println("Couldn't fetch backpack.");
            return new TF2Item[0];
        }
        try{
            s.fetch();
        }
        catch(Exception e) {
            System.out.println("Couldn't fetch data.");
        }
        inv = new TF2Item[s.size()];
        s.getItems().values().toArray(inv);
        return inv;
    }
    
    private static SteamId makeId(long user) {
        SteamId s = null;
        boolean c = true;
        long t = System.currentTimeMillis();
        while(c && System.currentTimeMillis()-t<15000) {
            try{
                s = SteamId.create(user);
                c = false;
            }
            catch(Exception e) {
            }
        }
        if(c) {
            System.out.println("Unable to get id of "+user+".");
            return null;
        }
        else {
            return s;
        }
    }
    
    private static SteamId makeId(String user) {
        SteamId s = null;
        boolean c = true;
        long t = System.currentTimeMillis();
        while(c && System.currentTimeMillis()-t<15000) {
            try{
                s = SteamId.create(user);
                c = false;
            }
            catch(Exception e) {
            }
        }
        if(c) {
            System.out.println("Unable to get friends of "+user+". Exiting...");
            return null;
        }
        else {
            return s;
        }
    }
    
    public static void treeIds(String user) {
        SteamId s;
        SteamId[] friends = null;
        s = makeId(user);
        int pos = 0;
        int[] tf = new int[friends.length];
        for(int i=0; i<friends.length; i++) {
            try{
                if(hasTF2(friends[i])) {
                    tf[pos++] = i;
                    System.out.println(friends[i].getSteamId64());
                }
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
        int chosen = new Random().nextInt(pos);
        if(++counter<3) {
            treeIds(Long.toString(friends[tf[chosen]].getSteamId64()));
        }
        else{
            counter = 0;
        }
    }
    
    public void getFriends(String user) throws Exception {
        SteamId m = SteamId.create(user);
        SteamId[] friends = m.getFriends();
        for(int i=0; i<friends.length; i++) {
            try{
                if(hasTF2(friends[i])) {
                    getBackpack(friends[i]);
                }
            }
            catch(com.github.koraktor.steamcondenser.exceptions.SteamCondenserException s) {
                System.out.println(s.getMessage());
            }
        } 
    }
    
    public static boolean hasTF2(SteamId user) throws com.github.koraktor.steamcondenser.exceptions.SteamCondenserException{
        long t = System.currentTimeMillis();
        while(System.currentTimeMillis()-t<15000) {
            try{
//                System.out.println("Trying to fetch games..."+(15-(double)(System.currentTimeMillis()-t)/1000)+" seconds left.");
                if(!user.isFetched()) {
                    user.fetchData();
                }
                return user.getGames().containsKey(440);
            }
            catch(Exception e) {}
        }
        throw new com.github.koraktor.steamcondenser.exceptions.SteamCondenserException("Unable to get "+user.getSteamId64()+"'s games.");
    }
    
    private static void staticBP(SteamId d) throws Exception {
        TF2Item[] inv;
        try{
            d.fetchData();
            System.out.println("\n==="+d.getNickname()+"'s backpack===\n");
            TF2Inventory bp = TF2Inventory.create(d.getSteamId64());
            inv = new TF2Item[bp.size()];
            bp.getItems().values().toArray(inv);
            for(TF2Item c:inv) {
                if(c.getSlot().equals("misc") || c.getSlot().equals("head") || !c.getQuality().equals("Unique")) {
                    System.out.print(rarity.containsKey(c.getQuality()) ? rarity.get(c.getQuality()) : c.getQuality());
                    System.out.println(" " + c.getName());
                }
            }
        }
        catch(Exception e){
            System.out.println(d.getSteamId64() +" "+e.getMessage());
        }
    }
}
