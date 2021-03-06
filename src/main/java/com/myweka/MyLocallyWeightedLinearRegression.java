package com.myweka;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.lazy.LWL;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.KDTree;

/**
 * 
 *
 */
public class MyLocallyWeightedLinearRegression {
    /** WEKA LWL object */
    protected static LWL lwl = new LWL();
    
    /* WEIGHTING KERNEL METHODS FOR LOCALLY WEIGHTED REGRESSION */
    
    /* corresponding constants in LWL are not accessible, already annotated, will be changed.
     * So we provide them here again. GAUSSIAN and INVERSE weighting from LWL does not work 
     * locally, only global over the whole data set, also annotated, will maybe be changed in 
     * near future. So, we only provide the remaining method constants, tests over the global 
     * data set showed, that there are not too big differences in the results in comparison to 
     * GAUSSIAN weighting. In tests using local weighting, LINEAR weighting performed very well, 
     * that's why it will be used by default. */
    
    /** Linear weighting function. */
    public static final int LINEAR = 0;
    /** Epanechnikov weighting function. */
    public static final int EPANECHNIKOV = 1;
    /** Tricube weighting function. */
    public static final int TRICUBE = 2;  
    /** Constant weighting function. */
    public static final int CONSTANT = 5;
    
    public static String dataFile = "data/chepai2015_2016.arff";
    
    public static void main(String[] args) throws Exception {
     // set the method for local regression
        lwl.setClassifier(new LinearRegression());
        // set number of nearest neighbours to be used for local prediction
        lwl.setKNN(6); //
        // set weighting kernel method (see comments on constants)
        lwl.setWeightingKernel(LINEAR);
        // set KDTree as nearest neighbour search method
        lwl.setNearestNeighbourSearchAlgorithm(new KDTree());
        
        // 读训练数据
        DataSource train_data = new DataSource(dataFile);
        // 获取训练数据集
        Instances insTrain = train_data.getDataSet();
        
        insTrain.deleteStringAttributes(); // if you want to filter out any attribute, set it's type to string
        
        System.out.println(insTrain.toString());
        
        // 设置训练集中，target的index(被预测的字段)
        insTrain.setClass(insTrain.attribute("LowestDistance")); 
        // build the classifier
        lwl.buildClassifier(insTrain);
        
        try {
            // 评估线性回归的结果
            Evaluation eval = new Evaluation(insTrain);
            eval.evaluateModel(lwl, insTrain);// 评估结果
            // 构造结果对象
            StringBuilder sb = new StringBuilder();
            sb.append("机器学习后产生的线性回归公式:\n" + lwl.toString() + "\n\n");
            sb.append("评估此结果:" + eval.toSummaryString() + "\n");
            System.out.println(sb.toString());
            
            int count = insTrain.numInstances();
            for (int i = 0; i < count; i++) {
                double ret = lwl.classifyInstance(insTrain.instance(i));
                System.out.println("Prediction & Actual for (LowestDistance-WarningPrice)= " + (int)ret + ", " + (insTrain.instance(i).value(insTrain.attribute("LowestDistance"))));
                //  (insTrain.instance(i).value(insTrain.attribute("LowPrice")) -insTrain.instance(i).value(insTrain.attribute("WarningPrice"))
            }
            
            // 预测(最后实际价格-11:29:42的实时价格)
            double[] queryVector = new double[]{11829,266007, 0};// FIX: 1. 增大”投放数量“ 反而减小输出值。
            Instance ins = new SparseInstance(1, queryVector);
            ins.setDataset(insTrain);
            double ret = lwl.classifyInstance(ins);
            System.out.println("预测需要加价 ： " + (int)ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

//    /**
//     * Make a prediction for a query vector (numeric classification).
//     * @param queryVector The input vector to predict the output value from.
//     * @return The predicted value.
//     * @throws Exception
//     */
//    public double predict(double[] queryVector) throws Exception
//    {
//        return predict(new DenseDoubleMatrix1D(queryVector));
//    }
//    
//    /**
//     * Make a prediction for a query vector in form of a WEKA instance.
//     * @param instance The WEKA instance to predict the output value from.
//     * @return The predicted value.
//     * @throws Exception
//     */
//    public double predict(Instance instance) throws Exception
//    {
//        /* refer instance to dataset, does not check if the instance is compatible 
//         * with the dataset (from javadocs), that's why we check it a step later
//         */
//        instance.setDataset(dataset);
//
//        if (!dataset.checkInstance(instance))
//        {
//            throw new Exception("Instance to predict is not compatible with the dataset!");
//        }
//
//        // predict the instance
//        return lwl.classifyInstance(instance);
//    }
    
}
