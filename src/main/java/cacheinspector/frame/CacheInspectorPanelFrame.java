package cacheinspector.frame;

import cacheinspector.core.Inspector;
import cacheinspector.core.InspectorCsv;
import cacheinspector.entity.CacheInspectorEntity;
import cacheinspector.panel.CacheInspectorCsvTreePanel;
import cacheinspector.panel.CacheInspectorTreePanel;
import cacheinspector.swing.ProgressBar;
import cacheinspector.swing.SwingProgressBar;
import cacheinspector.utility.ReadCsv;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class CacheInspectorPanelFrame extends JTabbedPane  implements Runnable{
    private static final String CLASS_NAME = "CacheInspectorPanelFrame";
    private JFrame frame;
    private String fileName;
    private String type;
    private final String CSV_TYPE = "csv";
    private final String SALESFORCE_TYPE = "salesforce";
    private Logger logger;

    public CacheInspectorPanelFrame(JFrame frame, String fileName, Logger logger) {
        setBackground(new Color(245, 130, 130));
        this.frame = frame;
        this.fileName = fileName;
        this.type = CSV_TYPE;
        this.logger = logger;
        Thread thread1 = new Thread(this);
        thread1.start();
    }

    public CacheInspectorPanelFrame(JFrame frame, Logger logger) {
        setBackground(new Color(245, 130, 130));
        this.frame = frame;
        this.type = SALESFORCE_TYPE;
        this.logger = logger;
        Thread thread1 = new Thread(this);
        thread1.start();
    }

    public void run()
    {
        String METHOD_NAME = "run";
        logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Run progressBar");
        ProgressBar prog = new ProgressBar(200,50,frame.getLocationOnScreen().x+frame.getSize().width/3 , this.getLocationOnScreen().y+frame.getSize().height/3, new Color(185,211,238), new Color(30,144,255));
        prog.setVisible(true);
        switch (type) {
            case CSV_TYPE -> {
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Read Csv");
                try {
                    ReadCsv readCsv = new ReadCsv();
                    readCsv.readFile(new File(fileName));
                    prog.setVisible(false);
                    logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Read products( " + InspectorCsv.getProductMap().size() + ") promotions(" + InspectorCsv.getPromoMap().size() + ")");
                    JComponent skyProducts = new CacheInspectorCsvTreePanel(InspectorCsv.getProductMap());
                    addTab("Products", skyProducts);
                    setMnemonicAt(0, KeyEvent.VK_1);
                    JComponent skyPromotions = new CacheInspectorCsvTreePanel(InspectorCsv.getPromoMap());
                    addTab("Promotions", skyPromotions);
                    setMnemonicAt(1, KeyEvent.VK_2);
                } catch (Exception e) {
                    logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Exception: " + e.getMessage());
                }
            }
            case SALESFORCE_TYPE -> {
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Retrieve records from Salesforce");
                Integer countId = Salesforce.queryCachedApiResponse();
                logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Retrieved " + countId + " records from Salesforce");
                prog.setVisible(false);
                int result = JOptionPane.showConfirmDialog(frame, countId + " records will be extracted, do you want to proceed?", "CacheInspector count(" + countId + ")",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** extraction confirmed");
                    SwingProgressBar swingProgressBar = new SwingProgressBar(frame.getLocationOnScreen().x + frame.getSize().width / 3, this.getLocationOnScreen().y + frame.getSize().height / 3, countId);
                    List<CacheInspectorEntity> cacheInspectorEntityList = Salesforce.queryCacheInspector(swingProgressBar);
                    swingProgressBar.setVisible(false);
                    ObjectMapper mapper = new ObjectMapper();
                    Inspector.clearMap();
                    for (CacheInspectorEntity cacheInspectorEntity : cacheInspectorEntityList) {
                        try {
                            Map<String, Object> root = mapper.readValue(cacheInspectorEntity.getJsonObject(), Map.class);
                            Inspector.readJson(cacheInspectorEntity.getCacheKey(), cacheInspectorEntity.getCatalogCode(), root);
                        } catch (Exception e) {
                            logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** Exception " + e.getMessage());
                        }
                    }
                    logger.info(CLASS_NAME + '.' + METHOD_NAME + " **** products( " + Inspector.getProductMap().size() + ") promotions(" + Inspector.getPromoMap().size() + ") catalogs(" + Inspector.getCatalogMap().size() + ")");
                    JComponent skyProducts = new CacheInspectorTreePanel(frame, Inspector.getProductMap(), CacheInspectorTreePanel.TYPE_PRODUCTS,  logger);
                    addTab("Products", skyProducts);
                    setMnemonicAt(0, KeyEvent.VK_1);
                    JComponent skyPromotions = new CacheInspectorTreePanel(frame, Inspector.getPromoMap(), CacheInspectorTreePanel.TYPE_PROMOTIONS, logger);
                    addTab("Promotions", skyPromotions);
                    setMnemonicAt(1, KeyEvent.VK_2);
                    JComponent skyCatalog = new CacheInspectorTreePanel(frame, Inspector.getCatalogMap(), CacheInspectorTreePanel.TYPE_CATALOGS, logger);
                    addTab("Catalogs", skyCatalog);
                    setMnemonicAt(2, KeyEvent.VK_3);
                    JComponent basketUIPanelFrame = new BasketUIPanelFrame();
                    addTab("BasketUI", basketUIPanelFrame);
                    setMnemonicAt(3, KeyEvent.VK_4);
                    setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                }
                break;
            }
        }

    }
}